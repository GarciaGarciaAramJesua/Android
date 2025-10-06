package com.example.practica3.data.repository

import android.content.Context
import android.os.Environment
import android.webkit.MimeTypeMap
import com.example.practica3.data.database.FavoriteFileDao
import com.example.practica3.data.database.RecentFileDao
import com.example.practica3.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val context: Context,
    private val recentFileDao: RecentFileDao,
    private val favoriteFileDao: FavoriteFileDao
) {
    
    suspend fun getFilesInDirectory(path: String): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val directory = File(path)
            if (!directory.exists() || !directory.isDirectory || !directory.canRead()) {
                return@withContext Result.failure(IOException("Cannot access directory: $path"))
            }

            val files = directory.listFiles()?.map { file ->
                FileItem.fromFile(file)
            }?.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() })
                ?: emptyList()

            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchFiles(query: String, searchPath: String): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            val searchResults = mutableListOf<FileItem>()
            val directory = File(searchPath)
            
            if (!directory.exists() || !directory.canRead()) {
                return@withContext Result.failure(IOException("Cannot access directory: $searchPath"))
            }

            searchInDirectory(directory, query.lowercase(), searchResults)
            Result.success(searchResults.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() }))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun searchInDirectory(directory: File, query: String, results: MutableList<FileItem>) {
        try {
            directory.listFiles()?.forEach { file ->
                if (file.name.lowercase().contains(query)) {
                    results.add(FileItem.fromFile(file))
                }
                if (file.isDirectory && file.canRead()) {
                    searchInDirectory(file, query, results)
                }
            }
        } catch (e: SecurityException) {
            // Skip directories we can't read
        }
    }

    suspend fun createFolder(parentPath: String, folderName: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val parentDir = File(parentPath)
            if (!parentDir.exists() || !parentDir.isDirectory || !parentDir.canWrite()) {
                return@withContext Result.failure(IOException("Cannot write to directory: $parentPath"))
            }

            val newFolder = File(parentDir, folderName)
            if (newFolder.exists()) {
                return@withContext Result.failure(IOException("Folder already exists"))
            }

            val success = newFolder.mkdir()
            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFile(filePath: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(IOException("File does not exist"))
            }

            val success = if (file.isDirectory) {
                deleteDirectoryRecursively(file)
            } else {
                file.delete()
            }

            if (success) {
                // Remove from recent files and favorites
                recentFileDao.deleteRecentFileByPath(filePath)
                favoriteFileDao.deleteFavoriteFileByPath(filePath)
            }

            Result.success(success)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun deleteDirectoryRecursively(directory: File): Boolean {
        try {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteDirectoryRecursively(file)
                } else {
                    file.delete()
                }
            }
            return directory.delete()
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun renameFile(oldPath: String, newName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val oldFile = File(oldPath)
            if (!oldFile.exists()) {
                return@withContext Result.failure(IOException("File does not exist"))
            }

            val newFile = File(oldFile.parent, newName)
            if (newFile.exists()) {
                return@withContext Result.failure(IOException("File with new name already exists"))
            }

            val success = oldFile.renameTo(newFile)
            if (success) {
                // Update recent files and favorites
                recentFileDao.deleteRecentFileByPath(oldPath)
                favoriteFileDao.deleteFavoriteFileByPath(oldPath)
                Result.success(newFile.absolutePath)
            } else {
                Result.failure(IOException("Failed to rename file"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun copyFile(sourcePath: String, destinationPath: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val sourceFile = File(sourcePath)
            val destFile = File(destinationPath)

            if (!sourceFile.exists()) {
                return@withContext Result.failure(IOException("Source file does not exist"))
            }

            if (destFile.exists()) {
                return@withContext Result.failure(IOException("Destination file already exists"))
            }

            if (sourceFile.isDirectory) {
                copyDirectoryRecursively(sourceFile, destFile)
            } else {
                copyFileContent(sourceFile, destFile)
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun copyDirectoryRecursively(sourceDir: File, destDir: File) {
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        sourceDir.listFiles()?.forEach { file ->
            val destFile = File(destDir, file.name)
            if (file.isDirectory) {
                copyDirectoryRecursively(file, destFile)
            } else {
                copyFileContent(file, destFile)
            }
        }
    }

    private fun copyFileContent(sourceFile: File, destFile: File) {
        FileInputStream(sourceFile).use { input ->
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        }
    }

    suspend fun moveFile(sourcePath: String, destinationPath: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val copyResult = copyFile(sourcePath, destinationPath)
            if (copyResult.isSuccess) {
                val deleteResult = deleteFile(sourcePath)
                if (deleteResult.isSuccess) {
                    Result.success(true)
                } else {
                    // Clean up copied file if delete failed
                    deleteFile(destinationPath)
                    Result.failure(IOException("Failed to delete source file after copy"))
                }
            } else {
                copyResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Recent files
    fun getRecentFiles(): Flow<List<RecentFile>> = recentFileDao.getAllRecentFiles()

    suspend fun addRecentFile(fileItem: FileItem) {
        val recentFile = RecentFile(
            path = fileItem.path,
            name = fileItem.name,
            mimeType = fileItem.mimeType
        )
        recentFileDao.insertRecentFile(recentFile)
        recentFileDao.limitRecentFiles()
    }

    // Favorites
    fun getFavoriteFiles(): Flow<List<FavoriteFile>> = favoriteFileDao.getAllFavoriteFiles()

    suspend fun addFavorite(fileItem: FileItem) {
        val favoriteFile = FavoriteFile(
            path = fileItem.path,
            name = fileItem.name,
            isDirectory = fileItem.isDirectory
        )
        favoriteFileDao.insertFavoriteFile(favoriteFile)
    }

    suspend fun removeFavorite(path: String) {
        favoriteFileDao.deleteFavoriteFileByPath(path)
    }

    suspend fun isFavorite(path: String): Boolean {
        return favoriteFileDao.isFavorite(path)
    }

    // Storage paths
    fun getInternalStoragePath(): String {
        return Environment.getExternalStorageDirectory().absolutePath
    }

    fun getExternalStoragePaths(): List<String> {
        val paths = mutableListOf<String>()
        try {
            val externalDirs = context.getExternalFilesDirs(null)
            externalDirs.forEach { dir ->
                dir?.let {
                    val rootPath = it.absolutePath.substringBefore("/Android")
                    if (File(rootPath).exists() && rootPath !in paths) {
                        paths.add(rootPath)
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback to primary external storage
            paths.add(Environment.getExternalStorageDirectory().absolutePath)
        }
        return paths
    }

    fun readTextFile(filePath: String): Result<String> {
        return try {
            val file = File(filePath)
            if (!file.exists() || !file.canRead()) {
                return Result.failure(IOException("Cannot read file: $filePath"))
            }
            val content = file.readText()
            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFileInfo(filePath: String): Result<FileItem> {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                return Result.failure(IOException("File does not exist: $filePath"))
            }
            Result.success(FileItem.fromFile(file))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}