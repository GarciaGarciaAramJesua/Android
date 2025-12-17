package com.example.action.ui.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.action.R
import com.example.action.data.local.AppDatabase
import com.example.action.data.local.entity.FavoriteEntity
import com.example.action.data.remote.RetrofitClient
import com.example.action.data.remote.model.OpenLibraryBook
import com.example.action.databinding.ActivityBookDetailBinding
import com.example.action.util.SessionManager
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import kotlinx.coroutines.launch

class BookDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabase
    private var currentBook: OpenLibraryBook? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        sessionManager = SessionManager(this)
        database = AppDatabase.getDatabase(this)

        // Obtener datos del libro desde el intent
        val bookJson = intent.getStringExtra("book_data")
        if (bookJson != null) {
            currentBook = Gson().fromJson(bookJson, OpenLibraryBook::class.java)
            displayBookDetails(currentBook!!)
            checkIfFavorite()
            loadBookDescription()
        } else {
            Toast.makeText(this, "Error al cargar detalles del libro", Toast.LENGTH_SHORT).show()
            finish()
        }

        setupListeners()
    }

    private fun displayBookDetails(book: OpenLibraryBook) {
        // Título
        binding.collapsingToolbar.title = book.title
        binding.tvTitle.text = book.title

        // Portada
        if (book.coverId != null) {
            val largeCoverUrl = "https://covers.openlibrary.org/b/id/${book.coverId}-L.jpg"
            Glide.with(this)
                .load(largeCoverUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(binding.ivCoverLarge)
        } else {
            binding.ivCoverLarge.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Autor
        val author = book.authorName?.joinToString(", ") ?: "Autor desconocido"
        binding.tvAuthor.text = author

        // Año de publicación
        if (book.firstPublishYear != null) {
            binding.layoutYear.visibility = View.VISIBLE
            binding.tvYear.text = book.firstPublishYear.toString()
        }

        // Editorial
        if (!book.publisher.isNullOrEmpty()) {
            binding.layoutPublisher.visibility = View.VISIBLE
            binding.tvPublisher.text = book.publisher.take(3).joinToString(", ")
        }

        // ISBN
        if (!book.isbn.isNullOrEmpty()) {
            binding.layoutIsbn.visibility = View.VISIBLE
            binding.tvIsbn.text = book.isbn.first()
        }

        // Páginas
        if (book.numberOfPages != null && book.numberOfPages > 0) {
            binding.layoutPages.visibility = View.VISIBLE
            binding.tvPages.text = book.numberOfPages.toString()
        }

        // Categorías/Temas
        if (!book.subject.isNullOrEmpty()) {
            binding.cardCategories.visibility = View.VISIBLE
            binding.chipGroupCategories.removeAllViews()
            
            book.subject.take(10).forEach { subject ->
                val chip = Chip(this)
                chip.text = subject
                chip.isClickable = false
                chip.isCheckable = false
                binding.chipGroupCategories.addView(chip)
            }
        }

        // Idiomas
        if (!book.language.isNullOrEmpty()) {
            val languages = book.language.joinToString(", ") { it.uppercase() }
            // Podrías agregar un TextView para idiomas si quieres mostrarlo
        }
    }

    private fun setupListeners() {
        binding.fabFavorite.setOnClickListener {
            if (isFavorite) {
                removeFavorite()
            } else {
                addToFavorites()
            }
        }
    }

    private fun checkIfFavorite() {
        currentBook?.let { book ->
            lifecycleScope.launch {
                val favorite = database.favoriteDao().getFavorite(
                    sessionManager.getUserId(),
                    book.getBookId()
                )
                isFavorite = favorite != null
                updateFavoriteButton()
            }
        }
    }

    private fun addToFavorites() {
        currentBook?.let { book ->
            lifecycleScope.launch {
                val favorite = FavoriteEntity(
                    userId = sessionManager.getUserId(),
                    bookId = book.getBookId(),
                    title = book.title,
                    author = book.getAuthor() ?: "Autor desconocido",
                    coverUrl = book.coverUrl,
                    addedAt = System.currentTimeMillis().toString(),
                    bookData = Gson().toJson(book) // Guardar datos completos del libro
                )
                
                database.favoriteDao().insertFavorite(favorite)
                isFavorite = true
                updateFavoriteButton()
                Toast.makeText(
                    this@BookDetailActivity,
                    "Agregado a favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun removeFavorite() {
        currentBook?.let { book ->
            lifecycleScope.launch {
                database.favoriteDao().deleteFavoriteByUserAndBook(
                    sessionManager.getUserId(),
                    book.getBookId()
                )
                isFavorite = false
                updateFavoriteButton()
                Toast.makeText(
                    this@BookDetailActivity,
                    "Eliminado de favoritos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateFavoriteButton() {
        if (isFavorite) {
            binding.fabFavorite.text = "Eliminar de Favoritos"
            binding.fabFavorite.setIconResource(android.R.drawable.btn_star_big_on)
        } else {
            binding.fabFavorite.text = "Agregar a Favoritos"
            binding.fabFavorite.setIconResource(android.R.drawable.btn_star_big_off)
        }
    }

    private fun loadBookDescription() {
        currentBook?.let { book ->
            // Primero intentar usar la descripción del modelo de búsqueda
            val simpleDescription = book.getDescription()
            if (!simpleDescription.isNullOrBlank()) {
                binding.cardDescription.visibility = View.VISIBLE
                binding.tvDescription.text = simpleDescription
            }
            
            // Luego intentar obtener descripción completa de la API
            lifecycleScope.launch {
                try {
                    val openLibraryService = RetrofitClient.openLibraryService
                    val response = openLibraryService.getWorkDetails(book.getBookId())
                    
                    if (response.isSuccessful && response.body() != null) {
                        val workDetails = response.body()!!
                        val fullDescription = workDetails.getDescriptionText()
                        
                        if (!fullDescription.isNullOrBlank()) {
                            binding.cardDescription.visibility = View.VISIBLE
                            binding.tvDescription.text = fullDescription
                        } else if (simpleDescription.isNullOrBlank()) {
                            // No hay descripción disponible
                            binding.cardDescription.visibility = View.GONE
                        }
                    }
                } catch (e: Exception) {
                    // Si falla, mantener la descripción simple o no mostrar nada
                    if (simpleDescription.isNullOrBlank()) {
                        binding.cardDescription.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
