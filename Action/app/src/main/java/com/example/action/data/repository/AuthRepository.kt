package com.example.action.data.repository

import com.example.action.data.Resource
import com.example.action.data.local.dao.UserDao
import com.example.action.data.local.entity.UserEntity
import com.example.action.data.remote.RetrofitClient
import com.example.action.data.remote.model.LoginRequest
import com.example.action.data.remote.model.LoginResponse
import com.example.action.data.remote.model.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val userDao: UserDao
) {
    private val apiService = RetrofitClient.apiService
    
    suspend fun register(username: String, password: String): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(RegisterRequest(username, password))
                if (response.isSuccessful) {
                    Resource.Success(response.body()?.message ?: "Registro exitoso")
                } else {
                    Resource.Error("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    suspend fun login(username: String, password: String): Resource<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null && loginResponse.status == "success") {
                        // Guardar usuario en la base de datos local
                        val user = UserEntity(
                            id = loginResponse.userId!!,
                            username = loginResponse.username!!,
                            isAdmin = loginResponse.isAdmin ?: false,
                            createdAt = null
                        )
                        userDao.insertUser(user)
                        Resource.Success(loginResponse)
                    } else {
                        Resource.Error(loginResponse?.message ?: "Error de login")
                    }
                } else {
                    Resource.Error("Credenciales inválidas")
                }
            } catch (e: Exception) {
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    suspend fun getUserById(userId: Int): UserEntity? {
        return withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        }
    }
}
