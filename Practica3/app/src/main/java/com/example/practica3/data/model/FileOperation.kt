package com.example.practica3.data.model

enum class FileOperationType {
    COPY,
    MOVE,
    DELETE,
    RENAME,
    CREATE_FOLDER
}

data class FileOperation(
    val type: FileOperationType,
    val sourceFiles: List<FileItem>,
    val destinationPath: String? = null,
    val newName: String? = null
)