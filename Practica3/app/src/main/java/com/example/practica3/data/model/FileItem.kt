package com.example.practica3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "file_items")
data class FileItem(
    @PrimaryKey
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val mimeType: String? = null,
    val isHidden: Boolean = false,
    val canRead: Boolean = true,
    val canWrite: Boolean = true,
    val parentPath: String? = null
) {
    companion object {
        fun fromFile(file: File): FileItem {
            return FileItem(
                path = file.absolutePath,
                name = file.name,
                isDirectory = file.isDirectory,
                size = if (file.isDirectory) 0 else file.length(),
                lastModified = file.lastModified(),
                mimeType = getMimeType(file),
                isHidden = file.isHidden,
                canRead = file.canRead(),
                canWrite = file.canWrite(),
                parentPath = file.parent
            )
        }

        private fun getMimeType(file: File): String? {
            return when (file.extension.lowercase()) {
                "txt", "log", "md" -> "text/plain"
                "json" -> "application/json"
                "xml" -> "application/xml"
                "jpg", "jpeg" -> "image/jpeg"
                "png" -> "image/png"
                "gif" -> "image/gif"
                "bmp" -> "image/bmp"
                "webp" -> "image/webp"
                "pdf" -> "application/pdf"
                "mp3" -> "audio/mpeg"
                "mp4" -> "video/mp4"
                "zip" -> "application/zip"
                else -> null
            }
        }
    }

    fun getFormattedSize(): String {
        if (isDirectory) return ""
        
        val kb = 1024
        val mb = kb * 1024
        val gb = mb * 1024
        
        return when {
            size >= gb -> String.format("%.1f GB", size.toDouble() / gb)
            size >= mb -> String.format("%.1f MB", size.toDouble() / mb)
            size >= kb -> String.format("%.1f KB", size.toDouble() / kb)
            else -> "$size B"
        }
    }

    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(lastModified))
    }

    fun isTextFile(): Boolean {
        return mimeType?.startsWith("text/") == true || 
               mimeType == "application/json" || 
               mimeType == "application/xml"
    }

    fun isImageFile(): Boolean {
        return mimeType?.startsWith("image/") == true
    }
    fun getFileTypeIcon(): Int {
        return when {
            isDirectory -> R.drawable.ic_folder
            isImageFile() -> R.drawable.ic_image_file
            isTextFile() -> R.drawable.ic_text_file
            else -> R.drawable.ic_file
        }
    }

}