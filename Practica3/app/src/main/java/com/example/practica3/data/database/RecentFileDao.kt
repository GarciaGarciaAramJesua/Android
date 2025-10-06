package com.example.practica3.data.database

import androidx.room.*
import com.example.practica3.data.model.RecentFile
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentFileDao {
    @Query("SELECT * FROM recent_files ORDER BY lastAccessed DESC LIMIT 50")
    fun getAllRecentFiles(): Flow<List<RecentFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentFile(recentFile: RecentFile)

    @Delete
    suspend fun deleteRecentFile(recentFile: RecentFile)

    @Query("DELETE FROM recent_files WHERE path = :path")
    suspend fun deleteRecentFileByPath(path: String)

    @Query("DELETE FROM recent_files")
    suspend fun clearAllRecentFiles()

    @Query("SELECT COUNT(*) FROM recent_files")
    suspend fun getRecentFilesCount(): Int

    @Query("DELETE FROM recent_files WHERE lastAccessed NOT IN (SELECT lastAccessed FROM recent_files ORDER BY lastAccessed DESC LIMIT 50)")
    suspend fun limitRecentFiles()
}