package com.example.action.data.repository

import android.util.Log
import com.example.action.data.Resource
import com.example.action.data.local.dao.BookDao
import com.example.action.data.local.dao.FavoriteDao
import com.example.action.data.local.dao.SearchHistoryDao
import com.example.action.data.local.entity.BookEntity
import com.example.action.data.local.entity.FavoriteEntity
import com.example.action.data.local.entity.SearchHistoryEntity
import com.example.action.data.remote.RetrofitClient
import com.example.action.data.remote.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class BookRepository(
    private val bookDao: BookDao,
    private val favoriteDao: FavoriteDao,
    private val searchHistoryDao: SearchHistoryDao
) {
    private val apiService = RetrofitClient.apiService
    private val openLibraryService = RetrofitClient.openLibraryService
    
    // Búsqueda en Open Library
    suspend fun searchBooks(query: String, userId: Int): Resource<List<OpenLibraryBook>> {
        return withContext(Dispatchers.IO) {
            try {
                // Guardar en historial
                saveSearchHistory(userId, query, "book")
                
                val response = openLibraryService.searchBooks(query)
                if (response.isSuccessful) {
                    val books = response.body()?.docs ?: emptyList()
                    
                    // Cachear en base de datos local
                    val bookEntities = books.map { book ->
                        BookEntity(
                            bookId = book.getBookId(),
                            title = book.title,
                            author = book.getAuthor(),
                            coverUrl = book.coverUrl,
                            firstPublishYear = book.firstPublishYear,
                            isbn = book.isbn?.firstOrNull()
                        )
                    }
                    bookDao.insertBooks(bookEntities)
                    
                    Resource.Success(books)
                } else {
                    Resource.Error("Error en la búsqueda")
                }
            } catch (e: Exception) {
                // Si falla, intentar cargar desde la base de datos local
                val cachedBooks = bookDao.getAllBooks()
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    suspend fun searchByAuthor(author: String, userId: Int): Resource<List<OpenLibraryBook>> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("BookRepository", "🔍 Buscando autor: $author")
                saveSearchHistory(userId, author, "author")
                
                val response = openLibraryService.searchByAuthor(author)
                Log.d("BookRepository", "📡 Response code: ${response.code()}")
                
                if (response.isSuccessful) {
                    val books = response.body()?.docs ?: emptyList()
                    Log.d("BookRepository", "📚 Libros encontrados: ${books.size}")
                    
                    if (books.isNotEmpty()) {
                        Log.d("BookRepository", "📖 Primer libro: ${books[0].title} por ${books[0].getAuthor()}")
                    }
                    
                    Resource.Success(books)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("BookRepository", "❌ Error response: $errorBody")
                    Resource.Error("Error en la búsqueda: código ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BookRepository", "💥 Excepción: ${e.message}", e)
                Resource.Error("Error de conexión: ${e.message}")
            }
        }
    }
    
    // Gestión de favoritos
    suspend fun addFavorite(
        userId: Int,
        bookId: String,
        title: String,
        author: String?,
        coverUrl: String?,
        bookData: String? = null
    ): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                
                // Guardar localmente primero
                val favoriteEntity = FavoriteEntity(
                    userId = userId,
                    bookId = bookId,
                    title = title,
                    author = author,
                    coverUrl = coverUrl,
                    addedAt = currentTime,
                    synced = false,
                    bookData = bookData
                )
                favoriteDao.insertFavorite(favoriteEntity)
                
                // Intentar sincronizar con el servidor
                try {
                    val request = FavoriteRequest(userId, bookId, title, author, coverUrl)
                    val response = apiService.addFavorite(request)
                    if (response.isSuccessful) {
                        // Marcar como sincronizado
                        favoriteDao.updateFavorite(favoriteEntity.copy(synced = true))
                    }
                } catch (e: Exception) {
                    // Si falla la sincronización, los datos están guardados localmente
                }
                
                Resource.Success("Agregado a favoritos")
            } catch (e: Exception) {
                Resource.Error("Error al agregar: ${e.message}")
            }
        }
    }
    
    fun getFavorites(userId: Int): Flow<List<FavoriteEntity>> {
        return favoriteDao.getFavoritesByUser(userId)
    }
    
    suspend fun removeFavorite(userId: Int, bookId: String): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                favoriteDao.deleteFavoriteByUserAndBook(userId, bookId)
                Resource.Success("Eliminado de favoritos")
            } catch (e: Exception) {
                Resource.Error("Error al eliminar: ${e.message}")
            }
        }
    }
    
    // Historial de búsqueda
    private suspend fun saveSearchHistory(userId: Int, query: String, searchType: String) {
        val currentTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
        val history = SearchHistoryEntity(
            userId = userId,
            query = query,
            searchType = searchType,
            searchedAt = currentTime,
            synced = false
        )
        searchHistoryDao.insertSearchHistory(history)
        
        // Intentar sincronizar con servidor
        try {
            val request = SearchHistoryRequest(userId, query, searchType)
            apiService.addSearchHistory(request)
        } catch (e: Exception) {
            // Ignorar errores de sincronización
        }
    }
    
    fun getSearchHistory(userId: Int): Flow<List<SearchHistoryEntity>> {
        return searchHistoryDao.getSearchHistoryByUser(userId)
    }
    
    // Recomendaciones
    suspend fun getRecommendations(userId: Int): Resource<RecommendationResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getRecommendations(userId)
                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(response.body()!!)
                } else {
                    Resource.Error("No hay recomendaciones disponibles")
                }
            } catch (e: Exception) {
                Resource.Error("Error: ${e.message}")
            }
        }
    }
    
    // Sincronización
    suspend fun syncData(userId: Int): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                // Sincronizar favoritos no sincronizados
                val unsyncedFavorites = favoriteDao.getUnsyncedFavorites()
                unsyncedFavorites.forEach { favorite ->
                    try {
                        val request = FavoriteRequest(
                            favorite.userId,
                            favorite.bookId,
                            favorite.title,
                            favorite.author,
                            favorite.coverUrl
                        )
                        apiService.addFavorite(request)
                        favoriteDao.updateFavorite(favorite.copy(synced = true))
                    } catch (e: Exception) {
                        // Continuar con el siguiente
                    }
                }
                
                // Sincronizar historial no sincronizado
                val unsyncedHistory = searchHistoryDao.getUnsyncedHistory()
                unsyncedHistory.forEach { history ->
                    try {
                        val request = SearchHistoryRequest(
                            history.userId,
                            history.query,
                            history.searchType
                        )
                        apiService.addSearchHistory(request)
                        searchHistoryDao.updateSearchHistory(history.copy(synced = true))
                    } catch (e: Exception) {
                        // Continuar con el siguiente
                    }
                }
                
                Resource.Success("Sincronización completada")
            } catch (e: Exception) {
                Resource.Error("Error de sincronización: ${e.message}")
            }
        }
    }
}
