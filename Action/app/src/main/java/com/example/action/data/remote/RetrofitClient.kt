package com.example.action.data.remote

import com.example.action.data.remote.api.ApiService
import com.example.action.data.remote.api.OpenLibraryService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    
    // IMPORTANTE: Cambia esta URL por la IP de tu máquina donde corre Flask
    // Si usas el emulador de Android: http://10.0.2.2:25565/ (para Docker con puerto 25565)
    // Si usas un dispositivo físico: http://TU_IP_LOCAL:25565/
    private const val BASE_URL = "http://10.100.74.93:25565/"
    private const val OPEN_LIBRARY_BASE_URL = "https://openlibrary.org/"
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    private val openLibraryRetrofit = Retrofit.Builder()
        .baseUrl(OPEN_LIBRARY_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    
    val openLibraryService: OpenLibraryService by lazy {
        openLibraryRetrofit.create(OpenLibraryService::class.java)
    }
}
