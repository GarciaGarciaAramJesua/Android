package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ThirdActivity : AppCompatActivity() {

    private lateinit var tvWelcome: TextView
    private lateinit var gridImages: GridLayout
    private lateinit var tvImageInfo: TextView
    private lateinit var btnBack: Button
    private lateinit var btnGoToMain: Button

    private val imageResources = arrayOf(
        android.R.drawable.ic_dialog_info to "Información",
        android.R.drawable.ic_dialog_alert to "Alerta",
        android.R.drawable.ic_menu_camera to "Cámara",
        android.R.drawable.ic_menu_gallery to "Galería",
        android.R.drawable.ic_menu_preferences to "Preferencias",
        android.R.drawable.ic_menu_save to "Guardar"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        
        initializeViews()
        setupWelcomeMessage()
        createImageGrid()
        setupListeners()
    }

    private fun initializeViews() {
        tvWelcome = findViewById(R.id.tvWelcome)
        gridImages = findViewById(R.id.gridImages)
        tvImageInfo = findViewById(R.id.tvImageInfo)
        btnBack = findViewById(R.id.btnBack)
        btnGoToMain = findViewById(R.id.btnGoToMain)
    }

    private fun setupWelcomeMessage() {
        val userName = intent.getStringExtra("userName") ?: "Usuario"
        tvWelcome.text = "🖼️ Bienvenido a la Galería, $userName!"
    }

    private fun createImageGrid() {
        imageResources.forEachIndexed { index, (imageRes, description) ->
            val imageView = ImageView(this).apply {
                setImageResource(imageRes)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 120
                    height = 120
                    setMargins(16, 16, 16, 16)
                }
                setPadding(8, 8, 8, 8)
                setBackgroundResource(android.R.drawable.btn_default)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                
                setOnClickListener {
                    showImageInfo(description, index + 1)
                }
            }
            gridImages.addView(imageView)
        }
    }

    private fun showImageInfo(description: String, position: Int) {
        val info = """
            🔍 Imagen Seleccionada:
            
            📋 Descripción: $description
            🔢 Posición: $position de ${imageResources.size}
            ⏰ Hora de selección: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}
        """.trimIndent()
        
        tvImageInfo.text = info
        tvImageInfo.visibility = android.view.View.VISIBLE
        
        Toast.makeText(this, "Imagen '$description' seleccionada", Toast.LENGTH_SHORT).show()
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish() // Volver a la activity anterior
        }

        btnGoToMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}