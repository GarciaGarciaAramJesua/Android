package com.example.action.ui.admin

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.action.data.Resource
import com.example.action.data.repository.AdminRepository
import com.example.action.databinding.ActivityAdminBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAdminBinding
    private lateinit var adminRepository: AdminRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Panel de Administración"
        
        adminRepository = AdminRepository()
        
        setupListeners()
        loadStats()
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
    
    private fun setupListeners() {
        binding.chipUsers.setOnClickListener {
            binding.chipUsers.isChecked = true
            binding.chipFavorites.isChecked = false
            binding.chipHistory.isChecked = false
            binding.chipStats.isChecked = false
            loadUsers()
        }
        
        binding.chipFavorites.setOnClickListener {
            binding.chipUsers.isChecked = false
            binding.chipFavorites.isChecked = true
            binding.chipHistory.isChecked = false
            binding.chipStats.isChecked = false
            loadAllFavorites()
        }
        
        binding.chipHistory.setOnClickListener {
            binding.chipUsers.isChecked = false
            binding.chipFavorites.isChecked = false
            binding.chipHistory.isChecked = true
            binding.chipStats.isChecked = false
            loadAllHistory()
        }
        
        binding.chipStats.setOnClickListener {
            binding.chipUsers.isChecked = false
            binding.chipFavorites.isChecked = false
            binding.chipHistory.isChecked = false
            binding.chipStats.isChecked = true
            loadStats()
        }
    }
    
    private fun loadUsers() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            when (val result = adminRepository.getAllUsers()) {
                is Resource.Success -> {
                    val users = result.data!!
                    val text = buildString {
                        append("Total de usuarios: ${users.size}\n\n")
                        users.forEach { user ->
                            append("ID: ${user.id}\n")
                            append("Usuario: ${user.username}\n")
                            append("Admin: ${if (user.isAdmin) "Sí" else "No"}\n")
                            append("Creado: ${user.createdAt ?: "N/A"}\n")
                            append("─────────────────────\n")
                        }
                    }
                    binding.tvContent.text = text
                }
                is Resource.Error -> {
                    Toast.makeText(this@AdminActivity, result.message, Toast.LENGTH_SHORT).show()
                    binding.tvContent.text = "Error al cargar usuarios"
                }
                else -> {}
            }
            binding.progressBar.visibility = View.GONE
        }
    }
    
    private fun loadAllFavorites() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            when (val result = adminRepository.getAllFavorites()) {
                is Resource.Success -> {
                    val favorites = result.data!!
                    val text = buildString {
                        append("Total de favoritos: ${favorites.size}\n\n")
                        favorites.forEach { fav ->
                            append("Usuario: ${fav["username"]}\n")
                            append("Libro: ${fav["title"]}\n")
                            append("Autor: ${fav["author"] ?: "N/A"}\n")
                            append("Agregado: ${fav["added_at"]}\n")
                            append("─────────────────────\n")
                        }
                    }
                    binding.tvContent.text = if (favorites.isEmpty()) {
                        "No hay favoritos registrados"
                    } else {
                        text
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this@AdminActivity, result.message, Toast.LENGTH_SHORT).show()
                    binding.tvContent.text = "Error al cargar favoritos"
                }
                else -> {}
            }
            binding.progressBar.visibility = View.GONE
        }
    }
    
    private fun loadAllHistory() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvContent.text = "Cargando historial..."
            
            when (val result = adminRepository.getAllSearchHistory()) {
                is Resource.Success -> {
                    val history = result.data!!
                    val text = buildString {
                        append("🔍 HISTORIAL DE BÚSQUEDAS\n\n")
                        append("Total: ${history.size} búsqueda(s)\n\n")
                        
                        if (history.isNotEmpty()) {
                            history.forEachIndexed { index, h ->
                                append("${index + 1}. ")
                                append("${h["username"]} buscó '${h["query"]}'\n")
                                append("   Tipo: ${h["search_type"]} | ${h["searched_at"]}\n")
                                if (index < history.size - 1) append("\n")
                            }
                        } else {
                            append("No hay búsquedas registradas aún.")
                        }
                    }
                    binding.tvContent.text = text
                }
                is Resource.Error -> {
                    val errorMsg = "Error al cargar historial:\n${result.message}"
                    binding.tvContent.text = errorMsg
                    Toast.makeText(this@AdminActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
            binding.progressBar.visibility = View.GONE
        }
    }
    
    private fun loadStats() {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvContent.text = "Cargando estadísticas..."
            
            when (val result = adminRepository.getStats()) {
                is Resource.Success -> {
                    val stats = result.data!!
                    val text = buildString {
                        append("📊 ESTADÍSTICAS DEL SISTEMA\n")
                        append("═══════════════════════════\n\n")
                        
                        append("👥 Usuarios registrados\n")
                        append("   ${stats.totalUsers} usuario(s)\n\n")
                        
                        append("❤️ Favoritos guardados\n")
                        append("   ${stats.totalFavorites} libro(s)\n\n")
                        
                        append("🔍 Búsquedas realizadas\n")
                        append("   ${stats.totalSearches} búsqueda(s)\n\n")
                        
                        if (stats.totalUsers > 0) {
                            val avgFavoritesPerUser = stats.totalFavorites.toFloat() / stats.totalUsers
                            val avgSearchesPerUser = stats.totalSearches.toFloat() / stats.totalUsers
                            
                            append("═══════════════════════════\n")
                            append("📈 PROMEDIOS POR USUARIO\n\n")
                            append("   Favoritos: ${"%.1f".format(avgFavoritesPerUser)}\n")
                            append("   Búsquedas: ${"%.1f".format(avgSearchesPerUser)}\n")
                        }
                    }
                    binding.tvContent.text = text
                }
                is Resource.Error -> {
                    val errorMsg = "❌ Error al cargar estadísticas\n\n${result.message}\n\nVerifica la conexión con el servidor."
                    binding.tvContent.text = errorMsg
                    Toast.makeText(this@AdminActivity, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
            binding.progressBar.visibility = View.GONE
        }
    }
}
