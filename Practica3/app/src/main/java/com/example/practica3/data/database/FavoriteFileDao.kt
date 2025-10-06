package com.example.practica3.data.database

import androidx.room.*
import com.example.practica3.data.model.FavoriteFile
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteFileDao {
    @Query("SELECT * FROM favorite_files ORDER BY addedDate DESC")
    fun getAllFavoriteFiles(): Flow<List<FavoriteFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteFile(favoriteFile: FavoriteFile)

    @Delete
    suspend fun deleteFavoriteFile(favoriteFile: FavoriteFile)

    @Query("DELETE FROM favorite_files WHERE path = :path")
    suspend fun deleteFavoriteFileByPath(path: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_files WHERE path = :path)")
    suspend fun isFavorite(path: String): Boolean

    @Query("DELETE FROM favorite_files")
    suspend fun clearAllFavorites()
}