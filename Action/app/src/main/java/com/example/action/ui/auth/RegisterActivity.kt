package com.example.action.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.action.data.Resource
import com.example.action.data.local.AppDatabase
import com.example.action.data.repository.AuthRepository
import com.example.action.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authRepository: AuthRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val database = AppDatabase.getDatabase(this)
        authRepository = AuthRepository(database.userDao())
        
        setupListeners()
    }
    
    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            performRegister(username, password)
        }
        
        binding.tvLogin.setOnClickListener {
            finish()
        }
    }
    
    private fun performRegister(username: String, password: String) {
        lifecycleScope.launch {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRegister.isEnabled = false
            
            when (val result = authRepository.register(username, password)) {
                is Resource.Success -> {
                    Toast.makeText(this@RegisterActivity, result.data, Toast.LENGTH_SHORT).show()
                    finish()
                }
                is Resource.Error -> {
                    Toast.makeText(this@RegisterActivity, result.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                }
                else -> {}
            }
        }
    }
}
