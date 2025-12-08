package com.example.action.data.remote.model

import com.google.gson.annotations.SerializedName

// Modelos para la API propia
data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val message: String,
    @SerializedName("user_id") val userId: Int?,
    val username: String?,
    @SerializedName("is_admin") val isAdmin: Boolean?
)

data class RegisterRequest(
    val username: String,
    val password: String
)

data class RegisterResponse(
    val message: String
)

data class FavoriteRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("book_id") val bookId: String,
    val title: String,
    val author: String?,
    @SerializedName("cover_url") val coverUrl: String?
)

data class FavoriteResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("book_id") val bookId: String,
    val title: String,
    val author: String?,
    @SerializedName("cover_url") val coverUrl: String?,
    @SerializedName("added_at") val addedAt: String
)

data class SearchHistoryRequest(
    @SerializedName("user_id") val userId: Int,
    val query: String,
    @SerializedName("search_type") val searchType: String
)

data class SearchHistoryResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val query: String,
    @SerializedName("search_type") val searchType: String,
    @SerializedName("searched_at") val searchedAt: String
)

data class RecommendationResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("recommended_authors") val recommendedAuthors: List<String>,
    @SerializedName("recent_searches") val recentSearches: List<String>,
    val message: String
)

data class UserResponse(
    val id: Int,
    val username: String,
    @SerializedName("is_admin") val isAdmin: Boolean,
    @SerializedName("created_at") val createdAt: String?
)

data class StatsResponse(
    @SerializedName("total_users") val totalUsers: Int,
    @SerializedName("total_favorites") val totalFavorites: Int,
    @SerializedName("total_searches") val totalSearches: Int
)
