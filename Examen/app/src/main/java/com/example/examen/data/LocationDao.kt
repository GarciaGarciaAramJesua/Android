package com.example.examen.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location: LocationEntity)
    
    @Query("SELECT * FROM locations ORDER BY timestamp DESC")
    fun getAllLocations(): Flow<List<LocationEntity>>
    
    @Query("SELECT * FROM locations ORDER BY timestamp ASC")
    fun getAllLocationsAscending(): Flow<List<LocationEntity>>
    
    @Query("DELETE FROM locations")
    suspend fun deleteAllLocations()
    
    @Query("SELECT COUNT(*) FROM locations")
    suspend fun getLocationCount(): Int
}
