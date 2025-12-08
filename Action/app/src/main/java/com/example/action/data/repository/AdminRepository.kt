package com.example.action.data.repository

import com.example.action.data.Resource
import com.example.action.data.remote.RetrofitClient
import com.example.action.data.remote.model.StatsResponse
import com.example.action.data.remote.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AdminRepository {
    private val apiService = RetrofitClient.apiService
    
    suspend fun getAllUsers(): Resource<List<UserResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllUsers()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Error al obtener usuarios")
                }
            } catch (e: Exception) {
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    suspend fun getAllFavorites(): Resource<List<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllFavorites()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Error al obtener favoritos")
                }
            } catch (e: Exception) {
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    suspend fun getAllSearchHistory(): Resource<List<Map<String, Any>>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getAllSearchHistory()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Error al obtener historial")
                }
            } catch (e: Exception) {
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    suspend fun getStats(): Resource<StatsResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStats()
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("Error al obtener estadísticas")
                }
            } catch (e: Exception) {
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
}
