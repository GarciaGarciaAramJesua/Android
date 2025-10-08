package com.example.practica2

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class FourthActivity : BaseActivity(), GestureDetector.OnGestureListener {
    private lateinit var gestureDetector: GestureDetector
    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100
    
    // Definir los equipos y sus activities (mismo orden que MainActivity)
    private val teams = listOf(
        TeamData("San Francisco 49ers", "#AA0000", "nfl_player_image", MainActivity::class.java),
        TeamData("Arizona Cardinals", "#97233F", "cardinals_image", SecondActivity::class.java),
        TeamData("Los Angeles Rams", "#003594", "rams_image", ThirdActivity::class.java),
        TeamData("Seattle Seahawks", "#002244", "seahawks_image", FourthActivity::class.java)
    )

    private val currentTeamIndex = 3 // FourthActivity es Seahawks (índice 3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fourth) // Layout específico para Seahawks

            // En MainActivity, SecondActivity, etc.
        val isDark = ThemeManager.isDarkMode(this)
            findViewById<View>(R.id.main)?.setBackgroundColor(
            ContextCompat.getColor(this,
            if (isDark) R.color.dark_background else R.color.light_background
            )
        )
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar detector de gestos
        gestureDetector = GestureDetector(this, this)
        
        // Configurar navegación por click
        setupTeamNavigation()

        // Aquí puedes agregar código específico para Seahawks
        setupSeahawksContent()
    }

    private fun setupSeahawksContent() {
        // Configurar contenido específico de Seahawks
        // Por ejemplo: estadísticas, jugadores, noticias, etc.
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (gestureDetector.onTouchEvent(event)) {
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 == null) return false
        
        val diffX = e2.x - e1.x
        val diffY = e2.y - e1.y
        
        if (abs(diffX) > abs(diffY)) {
            if (abs(diffX) > swipeThreshold && abs(velocityX) > swipeVelocityThreshold) {
                if (diffX > 0) {
                    onSwipeRight()
                } else {
                    onSwipeLeft()
                }
                return true
            }
        }
        return false
    }
    
    private fun onSwipeRight() {
        val previousIndex = if (currentTeamIndex == 0) teams.size - 1 else currentTeamIndex - 1
        navigateToTeam(previousIndex)
    }
    
    private fun onSwipeLeft() {
        val nextIndex = if (currentTeamIndex == teams.size - 1) 0 else currentTeamIndex + 1
        navigateToTeam(nextIndex)
    }
    
    private fun navigateToTeam(index: Int) {
        val team = teams[index]
        val intent = Intent(this, team.activityClass).apply {
            putExtra("TEAM_NAME", team.name)
            putExtra("TEAM_COLOR", team.color)
            putExtra("TEAM_BACKGROUND", team.backgroundImage)
            putExtra("CURRENT_INDEX", index)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish() // Opcional: para evitar acumular activities en el stack
    }

    private fun setupTeamNavigation() {
        // Navegación a info detallada al hacer click en la card activa (Seahawks)
        findViewById<androidx.cardview.widget.CardView>(R.id.cardSeahawksContainer).setOnClickListener {
            val intent = Intent(this, SeahawksInfoActivity::class.java).apply {
                putExtra("TEAM_NAME", "Seattle Seahawks")
                putExtra("TEAM_COLOR", "#002244")
                putExtra("TEAM_BACKGROUND", "seahawks_image")
                putExtra("TEAM_INDEX", 3)
            }
            startActivity(intent)
        }
        
        // Navegación por click a otras cards
        findViewById<androidx.cardview.widget.CardView>(R.id.card49ersContainer).setOnClickListener {
            navigateToTeam(0) // 49ers
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.cardCardinalsContainer).setOnClickListener {
            navigateToTeam(1) // Cardinals
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.cardRamsContainer).setOnClickListener {
            navigateToTeam(2) // Rams
        }
    }

    // Métodos requeridos por GestureDetector.OnGestureListener
    override fun onDown(e: MotionEvent): Boolean = true
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent) {}
    
    data class TeamData(
        val name: String,
        val color: String,
        val backgroundImage: String,
        val activityClass: Class<*>
    )
}