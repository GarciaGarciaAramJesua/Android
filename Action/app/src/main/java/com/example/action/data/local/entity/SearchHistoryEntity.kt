package com.example.action.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val query: String,
    val searchType: String, // "book" o "author"
    val searchedAt: String,
    val synced: Boolean = false
)
