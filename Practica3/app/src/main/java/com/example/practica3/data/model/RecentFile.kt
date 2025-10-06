package com.example.practica3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_files")
data class RecentFile(
    @PrimaryKey
    val path: String,
    val name: String,
    val lastAccessed: Long = System.currentTimeMillis(),
    val mimeType: String? = null
)