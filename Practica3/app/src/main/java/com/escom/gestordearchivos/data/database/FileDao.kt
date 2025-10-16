package com.escom.gestordearchivos.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones de archivos recientes
 */
@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY lastAccessed DESC LIMIT 50")
    fun getAllRecentFiles(): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentFile(file: RecentFileEntity)

    @Query("DELETE FROM recent_files WHERE path = :path")
    suspend fun deleteRecentFile(path: String)

    @Query("DELETE FROM recent_files")
    suspend fun clearAllRecentFiles()
}

/**
 * DAO para operaciones de archivos favoritos
 */
@Dao
interface FavoriteFileDao {
    @Query("SELECT * FROM favorite_files ORDER BY addedDate DESC")
    fun getAllFavorites(): Flow<List<FavoriteFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(file: FavoriteFileEntity)

    @Query("DELETE FROM favorite_files WHERE path = :path")
    suspend fun deleteFavorite(path: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_files WHERE path = :path)")
    suspend fun isFavorite(path: String): Boolean
}

/**
 * DAO para operaciones de caché de miniaturas
 */
@Dao
interface ThumbnailCacheDao {
    @Query("SELECT * FROM thumbnail_cache WHERE filePath = :filePath")
    suspend fun getThumbnail(filePath: String): ThumbnailCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThumbnail(thumbnail: ThumbnailCacheEntity)

    @Query("DELETE FROM thumbnail_cache WHERE createdAt < :timestamp")
    suspend fun deleteOldThumbnails(timestamp: Long)
}
