package com.example.practica3.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.practica3.data.model.FavoriteFile
import com.example.practica3.data.model.RecentFile

@Database(
    entities = [RecentFile::class, FavoriteFile::class],
    version = 1,
    exportSchema = false
)
abstract class FileExplorerDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun favoriteFileDao(): FavoriteFileDao

    companion object {
        @Volatile
        private var INSTANCE: FileExplorerDatabase? = null

        fun getDatabase(context: Context): FileExplorerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FileExplorerDatabase::class.java,
                    "file_explorer_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}