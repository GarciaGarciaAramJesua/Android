package com.example.action.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.action.R
import com.example.action.data.Resource
import com.example.action.data.local.AppDatabase
import com.example.action.data.repository.BookRepository
import com.example.action.databinding.ActivityHomeBinding
import com.example.action.ui.adapter.BookAdapter
import com.example.action.ui.adapter.FavoriteAdapter
import com.example.action.ui.admin.AdminActivity
import com.example.action.ui.auth.LoginActivity
import com.example.action.ui.detail.BookDetailActivity
import com.example.action.util.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeBinding
    private lateinit var bookRepository: BookRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var bookAdapter: BookAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter
    
    private val favoriteIds = mutableSetOf<String>()
    private var currentView = "search" // search, favorites, history, recommendations
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        val database = AppDatabase.getDatabase(this)
        bookRepository = BookRepository(
            database.bookDao(),
            database.favoriteDao(),
            database.searchHistoryDao()
        )
        sessionManager = SessionManager(this)
        
        setupAdapters()
        setupListeners()
        showSearchView()
        
        // Mostrar usuario en la barra
        supportActionBar?.title = "Hola, ${sessionManager.getUsername()}"
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        
        // Mostrar opción de admin solo si es administrador
        menu?.findItem(R.id.action_admin)?.isVisible = sessionManager.isAdmin()
        
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logout()
                true
            }
            R.id.action_admin -> {
                startActivity(Intent(this, AdminActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun setupAdapters() {
        bookAdapter = BookAdapter(
            onBookClick = { book ->
                openBookDetail(book)
            },
            onFavoriteClick = { book ->
                toggleFavorite(book)
            },
            getFavoriteIds = { favoriteIds.toSet() }
        )
        
        favoriteAdapter = FavoriteAdapter(
            onFavoriteClick = { favorite ->
                openFavoriteDetail(favorite)
            },
            onRemoveClick = { favorite ->
                removeFavorite(favorite)
            }
        )
        
        binding.rvBooks.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = bookAdapter
        }
        
        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = favoriteAdapter
        }
        
        binding.rvRecommendations.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = bookAdapter
        }
    }
    
    private fun setupListeners() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchBooks(query)
            } else {
                Toast.makeText(this, "Ingresa un término de búsqueda", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnSearchAuthor.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchByAuthor(query)
            } else {
                Toast.makeText(this, "Ingresa el nombre del autor", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.chipSearch.setOnClickListener { showSearchView() }
        binding.chipFavorites.setOnClickListener { showFavoritesView() }
        binding.chipHistory.setOnClickListener { showHistoryView() }
        binding.chipRecommendations.setOnClickListener { showRecommendations() }
    }
    
    private fun showSearchView() {
        currentView = "search"
        binding.layoutSearch.visibility = View.VISIBLE
        binding.rvBooks.visibility = View.VISIBLE
        binding.rvFavorites.visibility = View.GONE
        binding.rvRecommendations.visibility = View.GONE
        binding.cardRecommendations.visibility = View.GONE
        binding.cardHistory.visibility = View.GONE
        
        binding.chipSearch.isChecked = true
        binding.chipFavorites.isChecked = false
        binding.chipHistory.isChecked = false
        binding.chipRecommendations.isChecked = false
    }
    
    private fun showFavoritesView() {
        currentView = "favorites"
        binding.layoutSearch.visibility = View.GONE
        binding.rvBooks.visibility = View.GONE
        binding.rvFavorites.visibility = View.VISIBLE
        binding.rvRecommendations.visibility = View.GONE
        binding.cardRecommendations.visibility = View.GONE
        binding.cardHistory.visibility = View.GONE
        
        binding.chipSearch.isChecked = false
        binding.chipFavorites.isChecked = true
        binding.chipHistory.isChecked = false
        binding.chipRecommendations.isChecked = false
        
        loadFavorites()
    }
    
    private fun showHistoryView() {
        currentView = "history"
        binding.layoutSearch.visibility = View.GONE
        binding.rvBooks.visibility = View.GONE
        binding.rvFavorites.visibility = View.GONE
        binding.rvRecommendations.visibility = View.GONE
        binding.cardRecommendations.visibility = View.GONE
        binding.cardHistory.visibility = View.VISIBLE
        
        binding.chipSearch.isChecked = false
        binding.chipFavorites.isChecked = false
        binding.chipHistory.isChecked = true
        binding.chipRecommendations.isChecked = false
        
        loadHistory()
    }
    
    private fun showRecommendations() {
        currentView = "recommendations"
        binding.layoutSearch.visibility = View.GONE
        binding.rvBooks.visibility = View.GONE
        binding.rvFavorites.visibility = View.GONE
        binding.cardRecommendations.visibility = View.VISIBLE
        binding.rvRecommendations.visibility = View.GONE
        binding.cardHistory.visibility = View.GONE
        
        binding.chipSearch.isChecked = false
        binding.chipFavorites.isChecked = false
        binding.chipHistory.isChecked = false
        binding.chipRecommendations.isChecked = true
        
        loadRecommendations()
    }
    
    private fun searchBooks(query: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val result = bookRepository.searchBooks(query, sessionManager.getUserId())
            binding.progressBar.visibility = View.GONE
            
            when (result) {
                is Resource.Success -> {
                    if (result.data.isNullOrEmpty()) {
                        Toast.makeText(this@HomeActivity, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                    } else {
                        bookAdapter.submitList(result.data)
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this@HomeActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    private fun searchByAuthor(author: String) {
        Log.d("HomeActivity", "🔍 Búsqueda por autor iniciada: '$author'")
        
        if (author.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre de autor", Toast.LENGTH_SHORT).show()
            Log.w("HomeActivity", "⚠️ Búsqueda cancelada: campo vacío")
            return
        }
        
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            Log.d("HomeActivity", "📡 Llamando al repositorio...")
            
            val result = bookRepository.searchByAuthor(author, sessionManager.getUserId())
            binding.progressBar.visibility = View.GONE
            
            Log.d("HomeActivity", "📦 Resultado recibido: ${result::class.simpleName}")
            
            when (result) {
                is Resource.Success -> {
                    Log.d("HomeActivity", "✅ Success - Libros: ${result.data?.size ?: 0}")
                    
                    if (result.data.isNullOrEmpty()) {
                        Toast.makeText(this@HomeActivity, "No se encontraron resultados para '$author'", Toast.LENGTH_SHORT).show()
                        bookAdapter.submitList(emptyList())
                    } else {
                        Toast.makeText(this@HomeActivity, "${result.data.size} libro(s) encontrado(s)", Toast.LENGTH_SHORT).show()
                        bookAdapter.submitList(result.data)
                        Log.d("HomeActivity", "📚 Lista actualizada en el adaptador")
                    }
                }
                is Resource.Error -> {
                    Log.e("HomeActivity", "❌ Error: ${result.message}")
                    Toast.makeText(this@HomeActivity, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                    bookAdapter.submitList(emptyList())
                }
                else -> {
                    Log.w("HomeActivity", "⚠️ Resultado desconocido: $result")
                }
            }
        }
    }
    
    private fun toggleFavorite(book: com.example.action.data.remote.model.OpenLibraryBook) {
        lifecycleScope.launch {
            val bookId = book.getBookId()
            
            if (favoriteIds.contains(bookId)) {
                // Eliminar de favoritos
                val result = bookRepository.removeFavorite(sessionManager.getUserId(), bookId)
                when (result) {
                    is Resource.Success -> {
                        favoriteIds.remove(bookId)
                        bookAdapter.notifyDataSetChanged() // Actualizar UI
                        Toast.makeText(this@HomeActivity, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@HomeActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            } else {
                // Agregar a favoritos
                val result = bookRepository.addFavorite(
                    sessionManager.getUserId(),
                    bookId,
                    book.title,
                    book.getAuthor(),
                    book.coverUrl,
                    Gson().toJson(book)
                )
                
                when (result) {
                    is Resource.Success -> {
                        favoriteIds.add(bookId)
                        bookAdapter.notifyDataSetChanged() // Actualizar UI
                        Toast.makeText(this@HomeActivity, "Agregado a favoritos", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        Toast.makeText(this@HomeActivity, result.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }
    
    private fun loadFavorites() {
        lifecycleScope.launch {
            bookRepository.getFavorites(sessionManager.getUserId()).collect { favorites ->
                favoriteAdapter.submitList(favorites)
                
                // Actualizar Set de IDs de favoritos
                favoriteIds.clear()
                favoriteIds.addAll(favorites.map { it.bookId })
                bookAdapter.notifyDataSetChanged() // Actualizar estado en las cards
            }
        }
    }
    
    private fun removeFavorite(favorite: com.example.action.data.local.entity.FavoriteEntity) {
        lifecycleScope.launch {
            val result = bookRepository.removeFavorite(sessionManager.getUserId(), favorite.bookId)
            when (result) {
                is Resource.Success -> {
                    Toast.makeText(this@HomeActivity, result.data, Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    Toast.makeText(this@HomeActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    private fun loadHistory() {
        lifecycleScope.launch {
            bookRepository.getSearchHistory(sessionManager.getUserId()).collect { history ->
                val historyText = if (history.isEmpty()) {
                    "No hay historial de búsquedas"
                } else {
                    history.joinToString("\n") { 
                        "• ${it.query} (${it.searchType})" 
                    }
                }
                binding.tvHistory.text = historyText
            }
        }
    }
    
    private fun loadRecommendations() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.cardRecommendations.visibility = View.VISIBLE
            binding.tvRecommendations.text = "⏳ Cargando recomendaciones personalizadas..."
            
            val result = bookRepository.getRecommendations(sessionManager.getUserId())
            binding.progressBar.visibility = View.GONE
            
            when (result) {
                is Resource.Success -> {
                    val recommendations = result.data!!
                    val allBooks = mutableListOf<com.example.action.data.remote.model.OpenLibraryBook>()
                    
                    // Buscar libros de autores recomendados
                    if (recommendations.recommendedAuthors.isNotEmpty()) {
                        binding.tvRecommendations.text = "Searching books from recommended authors..."
                        
                        recommendations.recommendedAuthors.take(3).forEach { author ->
                            val booksResult = bookRepository.searchByAuthor(author, sessionManager.getUserId())
                            if (booksResult is Resource.Success) {
                                allBooks.addAll(booksResult.data?.take(5) ?: emptyList())
                            }
                        }
                    }
                    
                    // Buscar libros de búsquedas recientes
                    if (recommendations.recentSearches.isNotEmpty() && allBooks.size < 10) {
                        recommendations.recentSearches.take(2).forEach { search ->
                            val booksResult = bookRepository.searchBooks(search, sessionManager.getUserId())
                            if (booksResult is Resource.Success) {
                                allBooks.addAll(booksResult.data?.take(3) ?: emptyList())
                            }
                        }
                    }
                    
                    // Eliminar duplicados por key
                    val uniqueBooks = allBooks.distinctBy { it.key }.take(20)
                    
                    if (uniqueBooks.isNotEmpty()) {
                        binding.cardRecommendations.visibility = View.GONE
                        binding.rvRecommendations.visibility = View.VISIBLE
                        bookAdapter.submitList(uniqueBooks)
                    } else {
                        binding.cardRecommendations.visibility = View.VISIBLE
                        binding.rvRecommendations.visibility = View.GONE
                        binding.tvRecommendations.text = buildString {
                            append("You don't have recommendations available yet.\n\n")
                            append("Add favorites and search for books to get personalized recommendations.")
                        }
                    }
                }
                is Resource.Error -> {
                    binding.cardRecommendations.visibility = View.VISIBLE
                    binding.rvRecommendations.visibility = View.GONE
                    val errorText = "Error loading recommendations\n\n${result.message}\n\nCheck your internet connection."
                    binding.tvRecommendations.text = errorText
                    Toast.makeText(this@HomeActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }
    
    private fun openBookDetail(book: com.example.action.data.remote.model.OpenLibraryBook) {
        val intent = Intent(this, BookDetailActivity::class.java)
        intent.putExtra("book_data", Gson().toJson(book))
        startActivity(intent)
    }
    
    private fun openFavoriteDetail(favorite: com.example.action.data.local.entity.FavoriteEntity) {
        val intent = Intent(this, BookDetailActivity::class.java)
        
        // Si tiene los datos completos del libro, usar esos
        if (!favorite.bookData.isNullOrBlank()) {
            intent.putExtra("book_data", favorite.bookData)
        } else {
            // Si no, crear un objeto básico con la información disponible
            val basicBook = com.example.action.data.remote.model.OpenLibraryBook(
                key = "/works/${favorite.bookId}",
                title = favorite.title,
                authorName = favorite.author?.let { listOf(it) },
                firstPublishYear = null,
                coverId = null,
                isbn = null,
                publisher = null,
                language = null,
                subject = null,
                firstSentence = null,
                numberOfPages = null,
                directCoverUrl = favorite.coverUrl // Usar la URL guardada en el favorito
            )
            intent.putExtra("book_data", Gson().toJson(basicBook))
        }
        
        startActivity(intent)
    }
    
    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                sessionManager.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
