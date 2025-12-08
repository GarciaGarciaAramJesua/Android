package com.example.action.data.local.dao

import androidx.room.*
import com.example.action.data.local.entity.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE bookId = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?
    
    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)
    
    @Query("DELETE FROM books WHERE cachedAt < :threshold")
    suspend fun deleteOldBooks(threshold: Long)
}
