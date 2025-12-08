package com.example.action.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val bookId: String,
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val addedAt: String,
    val synced: Boolean = false
)
