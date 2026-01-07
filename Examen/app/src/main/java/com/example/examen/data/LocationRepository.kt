package com.example.examen.data

import kotlinx.coroutines.flow.Flow

class LocationRepository(private val locationDao: LocationDao) {
    
    val allLocations: Flow<List<LocationEntity>> = locationDao.getAllLocations()
    
    val allLocationsAscending: Flow<List<LocationEntity>> = locationDao.getAllLocationsAscending()
    
    suspend fun insert(location: LocationEntity) {
        locationDao.insertLocation(location)
    }
    
    suspend fun deleteAll() {
        locationDao.deleteAllLocations()
    }
    
    suspend fun getCount(): Int {
        return locationDao.getLocationCount()
    }
}
