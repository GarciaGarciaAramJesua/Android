package com.example.action.data.remote.api

import com.example.action.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    @POST("favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): Response<Map<String, Any>>
    
    @GET("favorites/{user_id}")
    suspend fun getFavorites(@Path("user_id") userId: Int): Response<List<FavoriteResponse>>
    
    @DELETE("favorites/{favorite_id}")
    suspend fun deleteFavorite(@Path("favorite_id") favoriteId: Int): Response<Map<String, String>>
    
    @POST("search-history")
    suspend fun addSearchHistory(@Body request: SearchHistoryRequest): Response<Map<String, Any>>
    
    @GET("search-history/{user_id}")
    suspend fun getSearchHistory(
        @Path("user_id") userId: Int,
        @Query("limit") limit: Int = 20
    ): Response<List<SearchHistoryResponse>>
    
    @GET("recommendations/{user_id}")
    suspend fun getRecommendations(@Path("user_id") userId: Int): Response<RecommendationResponse>
    
    // Admin endpoints
    @GET("admin/users")
    suspend fun getAllUsers(): Response<List<UserResponse>>
    
    @GET("admin/favorites")
    suspend fun getAllFavorites(): Response<List<Map<String, Any>>>
    
    @GET("admin/search-history")
    suspend fun getAllSearchHistory(): Response<List<Map<String, Any>>>
    
    @GET("admin/stats")
    suspend fun getStats(): Response<StatsResponse>
}
