package com.escom.gestordearchivos.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.FileProvider
import com.escom.gestordearchivos.domain.FileType
import java.io.File

/**
 * Utilidades para operaciones con archivos
 */
object FileUtils {

    /**
     * Obtiene el icono correspondiente según el tipo de archivo
     */
    fun getFileIcon(fileType: FileType): ImageVector {
        return when (fileType) {
            FileType.DIRECTORY -> Icons.Default.Folder
            FileType.TEXT -> Icons.Default.Description
            FileType.IMAGE -> Icons.Default.Image
            FileType.VIDEO -> Icons.Default.VideoFile
            FileType.AUDIO -> Icons.Default.AudioFile
            FileType.PDF -> Icons.Default.PictureAsPdf
            FileType.ARCHIVE -> Icons.Default.FolderZip
            FileType.APK -> Icons.Default.Android
            FileType.UNKNOWN -> Icons.Default.InsertDriveFile
        }
    }

    /**
     * Obtiene el tipo MIME de un archivo
     */
    fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) 
            ?: "application/octet-stream"
    }

    /**
     * Abre un archivo con una aplicación externa usando Intent
     */
    fun openFileWithExternalApp(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(file))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Abrir con..."))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Lee el contenido de un archivo de texto
     */
    fun readTextFile(file: File): Result<String> {
        return try {
            Result.success(file.readText())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Escribe contenido en un archivo de texto
     */
    fun writeTextFile(file: File, content: String): Result<Unit> {
        return try {
            file.writeText(content)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Valida si un nombre de archivo es válido
     */
    fun isValidFileName(name: String): Boolean {
        if (name.isBlank()) return false
        val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        return !name.any { it in invalidChars }
    }

    /**
     * Obtiene un nombre de archivo único si ya existe
     */
    fun getUniqueFileName(directory: File, baseName: String, extension: String = ""): String {
        var counter = 1
        var fileName = if (extension.isNotEmpty()) "$baseName.$extension" else baseName
        var file = File(directory, fileName)

        while (file.exists()) {
            fileName = if (extension.isNotEmpty()) {
                "${baseName}_$counter.$extension"
            } else {
                "${baseName}_$counter"
            }
            file = File(directory, fileName)
            counter++
        }

        return fileName
    }

    /**
     * Formatea el tamaño de archivo de forma legible
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    /**
     * Obtiene el path relativo desde una raíz
     */
    fun getRelativePath(root: File, file: File): String {
        return file.absolutePath.removePrefix(root.absolutePath)
            .removePrefix("/")
    }

    /**
     * Construye un breadcrumb path desde la raíz
     */
    fun getBreadcrumbs(root: File, current: File): List<Pair<String, File>> {
        val breadcrumbs = mutableListOf<Pair<String, File>>()
        var tempFile: File? = current

        while (tempFile != null && tempFile.absolutePath.startsWith(root.absolutePath)) {
            breadcrumbs.add(0, Pair(tempFile.name, tempFile))
            tempFile = tempFile.parentFile
        }

        if (breadcrumbs.isEmpty() || breadcrumbs.first().second != root) {
            breadcrumbs.add(0, Pair("Almacenamiento", root))
        }

        return breadcrumbs
    }

    /**
     * Verifica si un archivo de texto es demasiado grande para abrirlo
     */
    fun isTextFileTooLarge(file: File, maxSizeBytes: Long = 5 * 1024 * 1024): Boolean {
        return file.length() > maxSizeBytes
    }
}
