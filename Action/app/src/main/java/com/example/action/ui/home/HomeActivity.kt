package com.example.action.ui.home

import android.content.Intent
import android.os.Bundle
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
import com.example.action.util.SessionManager
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHomeBinding
    private lateinit var bookRepository: BookRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var bookAdapter: BookAdapter
    private lateinit var favoriteAdapter: FavoriteAdapter
    
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
                Toast.makeText(this, book.title, Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { book ->
                addToFavorites(book)
            }
        )
        
        favoriteAdapter = FavoriteAdapter { favorite ->
            removeFavorite(favorite)
        }
        
        binding.rvBooks.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = bookAdapter
        }
        
        binding.rvFavorites.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = favoriteAdapter
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
        binding.tvRecommendations.visibility = View.GONE
        binding.tvHistory.visibility = View.GONE
        
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
        binding.tvRecommendations.visibility = View.GONE
        binding.tvHistory.visibility = View.GONE
        
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
        binding.tvRecommendations.visibility = View.GONE
        binding.tvHistory.visibility = View.VISIBLE
        
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
        binding.tvRecommendations.visibility = View.VISIBLE
        binding.tvHistory.visibility = View.GONE
        
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
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            val result = bookRepository.searchByAuthor(author, sessionManager.getUserId())
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
    
    private fun addToFavorites(book: com.example.action.data.remote.model.OpenLibraryBook) {
        lifecycleScope.launch {
            val result = bookRepository.addFavorite(
                sessionManager.getUserId(),
                book.getBookId(),
                book.title,
                book.getAuthor(),
                book.getCoverUrl()
            )
            
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
    
    private fun loadFavorites() {
        lifecycleScope.launch {
            bookRepository.getFavorites(sessionManager.getUserId()).collect { favorites ->
                favoriteAdapter.submitList(favorites)
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
            val result = bookRepository.getRecommendations(sessionManager.getUserId())
            binding.progressBar.visibility = View.GONE
            
            when (result) {
                is Resource.Success -> {
                    val recommendations = result.data!!
                    val text = buildString {
                        append("Basado en tus favoritos y búsquedas:\n\n")
                        
                        if (recommendations.recommendedAuthors.isNotEmpty()) {
                            append("Autores recomendados:\n")
                            recommendations.recommendedAuthors.forEach { 
                                append("• $it\n") 
                            }
                            append("\n")
                        }
                        
                        if (recommendations.recentSearches.isNotEmpty()) {
                            append("Búsquedas recientes:\n")
                            recommendations.recentSearches.forEach { 
                                append("• $it\n") 
                            }
                        }
                        
                        if (recommendations.recommendedAuthors.isEmpty() && recommendations.recentSearches.isEmpty()) {
                            append("Aún no tienes favoritos o búsquedas para generar recomendaciones.")
                        }
                    }
                    binding.tvRecommendations.text = text
                }
                is Resource.Error -> {
                    binding.tvRecommendations.text = result.message
                }
                else -> {}
            }
        }
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
