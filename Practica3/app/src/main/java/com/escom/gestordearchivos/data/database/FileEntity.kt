package com.escom.gestordearchivos.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad para almacenar el historial de archivos recientes
 */
@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey
    val path: String,
    val name: String,
    val lastAccessed: Long,
    val fileType: String,
    val size: Long
)

/**
 * Entidad para almacenar archivos favoritos
 */
@Entity(tableName = "favorite_files")
data class FavoriteFileEntity(
    @PrimaryKey
    val path: String,
    val name: String,
    val addedDate: Long,
    val fileType: String
)

/**
 * Entidad para almacenar la caché de miniaturas
 */
@Entity(tableName = "thumbnail_cache")
data class ThumbnailCacheEntity(
    @PrimaryKey
    val filePath: String,
    val thumbnailPath: String,
    val createdAt: Long
)
