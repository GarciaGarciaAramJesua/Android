package com.example.practica3.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.File

object FileUtils {
    
    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast('.', "").lowercase()
    }

    fun getMimeType(file: File): String {
        val extension = getFileExtension(file.name)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "application/octet-stream"
    }

    fun isTextFile(file: File): Boolean {
        val textExtensions = setOf(
            "txt", "md", "log", "json", "xml", "csv", "html", "htm", 
            "js", "css", "java", "kt", "py", "cpp", "c", "h"
        )
        return textExtensions.contains(getFileExtension(file.name))
    }

    fun isImageFile(file: File): Boolean {
        val imageExtensions = setOf(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg"
        )
        return imageExtensions.contains(getFileExtension(file.name))
    }

    fun isVideoFile(file: File): Boolean {
        val videoExtensions = setOf(
            "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v"
        )
        return videoExtensions.contains(getFileExtension(file.name))
    }

    fun isAudioFile(file: File): Boolean {
        val audioExtensions = setOf(
            "mp3", "wav", "flac", "aac", "ogg", "wma", "m4a"
        )
        return audioExtensions.contains(getFileExtension(file.name))
    }

    fun isArchiveFile(file: File): Boolean {
        val archiveExtensions = setOf(
            "zip", "rar", "7z", "tar", "gz", "bz2", "xz"
        )
        return archiveExtensions.contains(getFileExtension(file.name))
    }

    fun openFileWithIntent(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, getMimeType(file))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "Abrir con...")
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareFile(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = getMimeType(file)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            val chooser = Intent.createChooser(intent, "Compartir archivo")
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        
        val units = arrayOf("KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = -1
        
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }
        
        return String.format("%.1f %s", size, units[unitIndex])
    }

    fun isValidFileName(name: String): Boolean {
        if (name.isBlank()) return false
        val invalidChars = setOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        return !name.any { it in invalidChars }
    }

    fun sanitizeFileName(name: String): String {
        val invalidChars = setOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        return name.filter { it !in invalidChars }.trim()
    }

    fun getFileIcon(file: File): Int {
        return when {
            file.isDirectory -> com.example.practica3.R.drawable.ic_folder
            isImageFile(file) -> com.example.practica3.R.drawable.ic_image_file
            isTextFile(file) -> com.example.practica3.R.drawable.ic_text_file
            else -> com.example.practica3.R.drawable.ic_file
        }
    }
}