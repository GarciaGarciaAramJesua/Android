package com.escom.gestordearchivos.domain

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Modelo de datos que representa un archivo o directorio en el sistema
 * @property file Objeto File del sistema
 * @property name Nombre del archivo o carpeta
 * @property path Ruta completa del archivo
 * @property isDirectory Indica si es un directorio
 * @property size Tamaño en bytes (0 para directorios)
 * @property lastModified Fecha de última modificación en milisegundos
 * @property extension Extensión del archivo (vacío para directorios)
 */
data class FileItem(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val isDirectory: Boolean = file.isDirectory,
    val size: Long = if (file.isDirectory) 0 else file.length(),
    val lastModified: Long = file.lastModified(),
    val extension: String = if (file.isDirectory) "" else file.extension.lowercase()
) {
    /**
     * Formatea el tamaño del archivo en formato legible (KB, MB, GB)
     */
    fun getFormattedSize(): String {
        if (isDirectory) return "--"
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "${size / 1024} KB"
            size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            else -> "${size / (1024 * 1024 * 1024)} GB"
        }
    }

    /**
     * Formatea la fecha de modificación
     */
    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date(lastModified))
    }

    /**
     * Determina el tipo de archivo basado en la extensión
     */
    fun getFileType(): FileType {
        return when {
            isDirectory -> FileType.DIRECTORY
            extension in listOf("txt", "md", "log", "json", "xml") -> FileType.TEXT
            extension in listOf("jpg", "jpeg", "png", "gif", "bmp", "webp") -> FileType.IMAGE
            extension in listOf("mp4", "avi", "mkv", "mov", "wmv") -> FileType.VIDEO
            extension in listOf("mp3", "wav", "ogg", "m4a", "flac") -> FileType.AUDIO
            extension in listOf("pdf") -> FileType.PDF
            extension in listOf("zip", "rar", "7z", "tar", "gz") -> FileType.ARCHIVE
            extension in listOf("apk") -> FileType.APK
            else -> FileType.UNKNOWN
        }
    }
}

/**
 * Enumeración de tipos de archivos soportados
 */
enum class FileType {
    DIRECTORY,
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    PDF,
    ARCHIVE,
    APK,
    UNKNOWN
}
