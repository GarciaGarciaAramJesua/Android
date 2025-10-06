package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private lateinit var etUserName: EditText
    private lateinit var etEmail: EditText
    private lateinit var spinnerTheme: Spinner
    private lateinit var switchNotifications: Switch
    private lateinit var btnSave: Button
    private lateinit var btnBack: Button
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        
        initializeViews()
        setupSpinner()
        setupListeners()
    }

    private fun initializeViews() {
        etUserName = findViewById(R.id.etUserName)
        etEmail = findViewById(R.id.etEmail)
        spinnerTheme = findViewById(R.id.spinnerTheme)
        switchNotifications = findViewById(R.id.switchNotifications)
        btnSave = findViewById(R.id.btnSave)
        btnBack = findViewById(R.id.btnBack)
        tvResult = findViewById(R.id.tvResult)
    }

    private fun setupSpinner() {
        val themes = arrayOf("Claro", "Oscuro", "Automático", "Azul", "Verde")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, themes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTheme.adapter = adapter
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            saveConfiguration()
        }

        btnBack.setOnClickListener {
            finish() // Volver a MainActivity
        }

        // Navegación a ThirdActivity
        findViewById<Button>(R.id.btnGoToThird).setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("userName", etUserName.text.toString())
            startActivity(intent)
        }
    }

    private fun saveConfiguration() {
        val userName = etUserName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val selectedTheme = spinnerTheme.selectedItem.toString()
        val notificationsEnabled = switchNotifications.isChecked

        if (userName.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val result = """
            ✅ Configuración Guardada:
            
            👤 Usuario: $userName
            📧 Email: $email
            🎨 Tema: $selectedTheme
            🔔 Notificaciones: ${if (notificationsEnabled) "Activadas" else "Desactivadas"}
        """.trimIndent()

        tvResult.text = result
        tvResult.visibility = android.view.View.VISIBLE

        Toast.makeText(this, "¡Configuración guardada exitosamente!", Toast.LENGTH_LONG).show()
    }
}