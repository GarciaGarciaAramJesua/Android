package com.example.action.data.local.dao

import androidx.room.*
import com.example.action.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM search_history WHERE userId = :userId ORDER BY searchedAt DESC LIMIT :limit")
    fun getSearchHistoryByUser(userId: Int, limit: Int = 20): Flow<List<SearchHistoryEntity>>
    
    @Query("SELECT * FROM search_history WHERE synced = 0")
    suspend fun getUnsyncedHistory(): List<SearchHistoryEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistoryEntity): Long
    
    @Update
    suspend fun updateSearchHistory(history: SearchHistoryEntity)
    
    @Query("DELETE FROM search_history WHERE userId = :userId")
    suspend fun deleteHistoryByUser(userId: Int)
}
