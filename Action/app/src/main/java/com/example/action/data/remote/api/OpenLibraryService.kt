package com.example.action.data.remote.api

import com.example.action.data.remote.model.OpenLibraryAuthorResponse
import com.example.action.data.remote.model.OpenLibrarySearchResponse
import com.example.action.data.remote.model.OpenLibraryWorkDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryService {
    
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("page") page: Int = 1
    ): Response<OpenLibrarySearchResponse>
    
    @GET("search.json")
    suspend fun searchByTitle(
        @Query("title") title: String,
        @Query("limit") limit: Int = 20
    ): Response<OpenLibrarySearchResponse>
    
    @GET("search.json")
    suspend fun searchByAuthor(
        @Query("q") author: String,
        @Query("limit") limit: Int = 20
    ): Response<OpenLibrarySearchResponse>
    
    @GET("search/authors.json")
    suspend fun searchAuthors(
        @Query("q") query: String,
        @Query("limit") limit: Int = 10
    ): Response<OpenLibraryAuthorResponse>
    
    @GET("works/{workId}.json")
    suspend fun getWorkDetails(
        @Path("workId") workId: String
    ): Response<OpenLibraryWorkDetails>
}
