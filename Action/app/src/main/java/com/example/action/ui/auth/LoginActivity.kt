package com.example.action.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.action.R
import com.example.action.data.Resource
import com.example.action.data.local.AppDatabase
import com.example.action.data.repository.AuthRepository
import com.example.action.databinding.ActivityLoginBinding
import com.example.action.ui.home.HomeActivity
import com.example.action.util.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Inicializar
        val database = AppDatabase.getDatabase(this)
        authRepository = AuthRepository(database.userDao())
        sessionManager = SessionManager(this)
        
        // Verificar si ya hay sesión
        if (sessionManager.isLoggedIn()) {
            navigateToHome()
            return
        }
        
        setupListeners()
    }
    
    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            performLogin(username, password)
        }
        
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    
    private fun performLogin(username: String, password: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnLogin.isEnabled = false
            
            when (val result = authRepository.login(username, password)) {
                is Resource.Success -> {
                    val loginResponse = result.data!!
                    sessionManager.saveLoginSession(
                        loginResponse.userId!!,
                        loginResponse.username!!,
                        loginResponse.isAdmin ?: false
                    )
                    Toast.makeText(this@LoginActivity, "Bienvenido!", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                }
                is Resource.Error -> {
                    Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
                else -> {}
            }
        }
    }
    
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
