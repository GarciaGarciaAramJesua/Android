package com.example.project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.project.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inicializar Firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        
        // Autenticación anónima
        signInAnonymously()
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        setupNavigation()
    }
    
    private fun signInAnonymously() {
        if (auth.currentUser == null) {
            auth.signInAnonymously()
                .addOnSuccessListener {
                    Log.d("Firebase", "Autenticación anónima exitosa")
                    testFirestoreConnection()
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error en autenticación: ${e.message}", e)
                    Toast.makeText(this, "Error Auth: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            testFirestoreConnection()
        }
    }
    
    private fun testFirestoreConnection() {
        val db = FirebaseFirestore.getInstance()
        
        // Configurar opciones de Firestore
        val settings = com.google.firebase.firestore.FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
        db.firestoreSettings = settings
        
        // Intentar leer de Firestore
        db.collection("test").limit(1).get()
            .addOnSuccessListener {
                Log.d("Firebase", "Conexión exitosa a Firestore")
                Toast.makeText(this, "Firebase conectado OK", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error de conexión: ${e.message}", e)
                Toast.makeText(this, "Error Firebase: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        binding.bottomNavigation.setupWithNavController(navController)
        
        // Sincronizar la navegación con el toolbar
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.toolbar.title = when (destination.id) {
                R.id.mapFragment -> "Mapa de Incidencias"
                R.id.newReportFragment -> "Nuevo Reporte"
                R.id.directoryFragment -> "Directorio de Instituciones"
                else -> "Participación Ciudadana"
            }
        }
    }
}
