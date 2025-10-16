package com.escom.gestordearchivos.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Base de datos principal de la aplicación usando Room
 * Almacena historial de archivos recientes, favoritos y caché de miniaturas
 */
@Database(
    entities = [
        RecentFileEntity::class,
        FavoriteFileEntity::class,
        ThumbnailCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FileManagerDatabase : RoomDatabase() {
    abstract fun recentFileDao(): RecentFileDao
    abstract fun favoriteFileDao(): FavoriteFileDao
    abstract fun thumbnailCacheDao(): ThumbnailCacheDao

    companion object {
        @Volatile
        private var INSTANCE: FileManagerDatabase? = null

        fun getDatabase(context: Context): FileManagerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FileManagerDatabase::class.java,
                    "file_manager_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
