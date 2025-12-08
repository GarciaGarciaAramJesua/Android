package com.example.action.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val bookId: String,
    val title: String,
    val author: String?,
    val coverUrl: String?,
    val firstPublishYear: Int?,
    val isbn: String?,
    val cachedAt: Long = System.currentTimeMillis()
)
