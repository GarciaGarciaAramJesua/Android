package com.example.action.data.local.dao

import androidx.room.*
import com.example.action.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun getFavoritesByUser(userId: Int): Flow<List<FavoriteEntity>>
    
    @Query("SELECT * FROM favorites WHERE userId = :userId AND bookId = :bookId")
    suspend fun getFavorite(userId: Int, bookId: String): FavoriteEntity?
    
    @Query("SELECT * FROM favorites WHERE synced = 0")
    suspend fun getUnsyncedFavorites(): List<FavoriteEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity): Long
    
    @Update
    suspend fun updateFavorite(favorite: FavoriteEntity)
    
    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)
    
    @Query("DELETE FROM favorites WHERE userId = :userId AND bookId = :bookId")
    suspend fun deleteFavoriteByUserAndBook(userId: Int, bookId: String)
}
