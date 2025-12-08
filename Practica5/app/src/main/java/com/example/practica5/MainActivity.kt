package com.example.practica5

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // Mostrar estado de conexión a Firebase en la UI.
        try {
            val statusText = findViewById<TextView>(R.id.firebaseStatusText)
            val connected = runCatching { FirebaseApp.getApps(this).isNotEmpty() }.getOrElse { false }
            if (connected) {
                // Mensaje claro que la conexión ya fue configurada.
                statusText.text = "Conexión con Firebase detectada y activa (ya configurada)."
            } else {
                statusText.text = "Firebase no inicializado en esta app."
            }
        } catch (e: Exception) {
            // En caso de que no exista la clase FirebaseApp o ocurra un error, mostrar mensaje genérico
            runCatching {
                findViewById<TextView>(R.id.firebaseStatusText).text = "Estado de Firebase: no disponible en tiempo de ejecución."
            }
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}