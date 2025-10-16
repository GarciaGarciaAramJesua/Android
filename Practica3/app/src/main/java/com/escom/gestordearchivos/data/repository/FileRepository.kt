package com.escom.gestordearchivos.data.repository

import android.content.Context
import android.os.Build
import android.os.Environment
import com.escom.gestordearchivos.data.database.*
import com.escom.gestordearchivos.domain.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * Repositorio que gestiona todas las operaciones con archivos
 * Implementa la lógica de negocio para navegación, gestión y persistencia
 */
class FileRepository(context: Context) {
    private val database = FileManagerDatabase.getDatabase(context)
    private val recentFileDao = database.recentFileDao()
    private val favoriteFileDao = database.favoriteFileDao()
    private val thumbnailCacheDao = database.thumbnailCacheDao()
    private val appContext = context.applicationContext

    /**
     * Obtiene el directorio raíz del almacenamiento
     */
    fun getRootDirectory(): File {
        // Si la app tiene MANAGE_EXTERNAL_STORAGE (Android 11+) o estamos en un entorno
        // donde se espera acceso global, podemos intentar devolver la raíz externa.
        // En caso contrario, devolvemos un directorio accesible para la app (fallback)
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                Environment.isExternalStorageManager()
            ) {
                Environment.getExternalStorageDirectory()
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                Environment.getExternalStorageDirectory()
            } else {
                // Fallback seguro: directorio externo específico de la app
                appContext.getExternalFilesDir(null) ?: appContext.filesDir
            }
        } catch (e: Exception) {
            // Fallback en caso de cualquier excepción
            appContext.getExternalFilesDir(null) ?: appContext.filesDir
        }
    }

    /**
     * Lista todos los archivos y carpetas en un directorio
     * @param directory Directorio a listar
     * @return Lista de FileItem ordenada (carpetas primero, luego archivos)
     */
    suspend fun listFiles(directory: File): Result<List<FileItem>> = withContext(Dispatchers.IO) {
        try {
            if (!directory.exists() || !directory.isDirectory) {
                return@withContext Result.failure(Exception("El directorio no existe o no es válido"))
            }

            val files = directory.listFiles()?.map { file ->
                FileItem(file)
            }?.sortedWith(
                compareBy<FileItem> { !it.isDirectory }
                    .thenBy { it.name.lowercase() }
            ) ?: emptyList()

            Result.success(files)
        } catch (e: SecurityException) {
            Result.failure(Exception("No tienes permisos para acceder a este directorio"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca archivos por nombre en un directorio y sus subdirectorios
     */
    suspend fun searchFiles(directory: File, query: String): Result<List<FileItem>> = 
        withContext(Dispatchers.IO) {
            try {
                val results = mutableListOf<FileItem>()
                searchRecursive(directory, query.lowercase(), results)
                Result.success(results.sortedBy { it.name.lowercase() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun searchRecursive(directory: File, query: String, results: MutableList<FileItem>) {
        try {
            directory.listFiles()?.forEach { file ->
                if (file.name.lowercase().contains(query)) {
                    results.add(FileItem(file))
                }
                if (file.isDirectory && results.size < 100) { // Limitar resultados
                    searchRecursive(file, query, results)
                }
            }
        } catch (e: SecurityException) {
            // Ignorar directorios sin permisos
        }
    }

    /**
     * Crea una nueva carpeta
     */
    suspend fun createFolder(parentDir: File, folderName: String): Result<File> = 
        withContext(Dispatchers.IO) {
            try {
                val newFolder = File(parentDir, folderName)

                if (!parentDir.exists()) {
                    return@withContext Result.failure(Exception("El directorio padre no existe"))
                }

                if (!parentDir.canWrite()) {
                    return@withContext Result.failure(Exception("Sin permisos para escribir en la ubicación seleccionada"))
                }

                if (newFolder.exists()) {
                    return@withContext Result.failure(Exception("Ya existe una carpeta con ese nombre"))
                }

                // Crear jerarquía si es necesario
                if (newFolder.mkdirs()) {
                    Result.success(newFolder)
                } else {
                    Result.failure(Exception("No se pudo crear la carpeta (posible restricción de permisos o ruta inválida)"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Renombra un archivo o carpeta
     */
    suspend fun renameFile(file: File, newName: String): Result<File> = 
        withContext(Dispatchers.IO) {
            try {
                val newFile = File(file.parent, newName)
                if (newFile.exists()) {
                    return@withContext Result.failure(Exception("Ya existe un archivo con ese nombre"))
                }
                if (file.renameTo(newFile)) {
                    Result.success(newFile)
                } else {
                    Result.failure(Exception("No se pudo renombrar el archivo"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Copia un archivo o carpeta
     */
    suspend fun copyFile(source: File, destinationDir: File): Result<File> = 
        withContext(Dispatchers.IO) {
            try {
                val destination = File(destinationDir, source.name)
                if (destination.exists()) {
                    return@withContext Result.failure(Exception("Ya existe un archivo con ese nombre"))
                }

                if (source.isDirectory) {
                    copyDirectory(source, destination)
                } else {
                    source.copyTo(destination, overwrite = false)
                }
                Result.success(destination)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Copia un archivo o carpeta resolviendo colisiones de nombres generando sufijos
     */
    suspend fun copyFileWithResolvedName(source: File, destinationDir: File): Result<File> =
        withContext(Dispatchers.IO) {
            try {
                // Generar nombre único si existe
                var targetName = source.name
                val dot = source.name.lastIndexOf('.')
                val (base, ext) = if (dot > 0) {
                    source.name.substring(0, dot) to source.name.substring(dot)
                } else {
                    source.name to ""
                }

                var candidate = File(destinationDir, targetName)
                var idx = 1
                while (candidate.exists()) {
                    targetName = if (ext.isNotEmpty()) {
                        "$base (copia $idx)$ext"
                    } else {
                        "$base (copia $idx)"
                    }
                    candidate = File(destinationDir, targetName)
                    idx++
                }

                if (source.isDirectory) {
                    copyDirectory(source, candidate)
                } else {
                    source.copyTo(candidate, overwrite = false)
                }
                Result.success(candidate)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    private fun copyDirectory(source: File, destination: File) {
        if (!destination.exists()) {
            destination.mkdirs()
        }
        source.listFiles()?.forEach { file ->
            val newFile = File(destination, file.name)
            if (file.isDirectory) {
                copyDirectory(file, newFile)
            } else {
                file.copyTo(newFile, overwrite = false)
            }
        }
    }

    /**
     * Mueve un archivo o carpeta
     */
    suspend fun moveFile(source: File, destinationDir: File): Result<File> = 
        withContext(Dispatchers.IO) {
            try {
                val destination = File(destinationDir, source.name)
                if (destination.exists()) {
                    return@withContext Result.failure(Exception("Ya existe un archivo con ese nombre"))
                }
                if (source.renameTo(destination)) {
                    Result.success(destination)
                } else {
                    Result.failure(Exception("No se pudo mover el archivo"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Elimina un archivo o carpeta
     */
    suspend fun deleteFile(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Métodos para archivos recientes
    fun getRecentFiles(): Flow<List<RecentFileEntity>> = recentFileDao.getAllRecentFiles()

    suspend fun addRecentFile(fileItem: FileItem) {
        recentFileDao.insertRecentFile(
            RecentFileEntity(
                path = fileItem.path,
                name = fileItem.name,
                lastAccessed = System.currentTimeMillis(),
                fileType = fileItem.extension,
                size = fileItem.size
            )
        )
    }

    suspend fun clearRecentFiles() = recentFileDao.clearAllRecentFiles()

    // Métodos para favoritos
    fun getFavorites(): Flow<List<FavoriteFileEntity>> = favoriteFileDao.getAllFavorites()

    suspend fun addFavorite(fileItem: FileItem) {
        favoriteFileDao.insertFavorite(
            FavoriteFileEntity(
                path = fileItem.path,
                name = fileItem.name,
                addedDate = System.currentTimeMillis(),
                fileType = fileItem.extension
            )
        )
    }

    suspend fun removeFavorite(path: String) = favoriteFileDao.deleteFavorite(path)

    suspend fun isFavorite(path: String): Boolean = favoriteFileDao.isFavorite(path)
}
