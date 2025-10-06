package com.example.practica3.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_files")
data class FavoriteFile(
    @PrimaryKey
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val addedDate: Long = System.currentTimeMillis()
)