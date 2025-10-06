package com.example.practica2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.abs

class MainActivity : AppCompatActivity(), GestureDetector.OnGestureListener {
    
    private lateinit var gestureDetector: GestureDetector
    private val swipeThreshold = 100
    private val swipeVelocityThreshold = 100
    
    // Definir los equipos y sus activities
    private val teams = listOf(
        TeamData("San Francisco 49ers", "#AA0000", "nfl_player_image", MainActivity::class.java),
        TeamData("Arizona Cardinals", "#97233F", "cardinals_image", SecondActivity::class.java),
        TeamData("Los Angeles Rams", "#003594", "rams_image", ThirdActivity::class.java),
        TeamData("Seattle Seahawks", "#002244", "seahawks_image", FourthActivity::class.java)
    )
    
    private val currentTeamIndex = 0 // MainActivity es 49ers (índice 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val activeIndicator = findViewById<View>(R.id.activeIndicator)
        activeIndicator.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))

        val card49ers = findViewById<CardView>(R.id.card49ersContainer)
        card49ers.animate().alpha(0f)
        card49ers.translationY = 100f
        card49ers.animate().alpha(1f).translationY(0f).setDuration(800).setStartDelay(200).start()

        // Configurar detector de gestos
        gestureDetector = GestureDetector(this, this)
        
        // Configurar navegación por click (opcional, mantiene funcionalidad actual)
        setupTeamNavigation()
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
                    // Swipe hacia la derecha - ir al equipo anterior
                    onSwipeRight()
                } else {
                    // Swipe hacia la izquierda - ir al siguiente equipo
                    onSwipeLeft()
                }
                return true
            }
        }
        return false
    }
    
    private fun onSwipeRight() {
        // Ir al equipo anterior (con ciclo)
        val previousIndex = if (currentTeamIndex == 0) teams.size - 1 else currentTeamIndex - 1
        navigateToTeam(previousIndex)
    }
    
    private fun onSwipeLeft() {
        // Ir al siguiente equipo (con ciclo)
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
        // Animación de transición suave
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    private fun setupTeamNavigation() {
        // Navegación a info detallada al hacer click en la card activa (Cardinals)
        findViewById<androidx.cardview.widget.CardView>(R.id.card49ersContainer).setOnClickListener {
            val intent = Intent(this, NinersInfoActivity::class.java).apply {
                putExtra("TEAM_NAME", "San Francisco 49ers")
                putExtra("TEAM_COLOR", "#AA0000")
                putExtra("TEAM_BACKGROUND", "49ers_image")
                putExtra("TEAM_INDEX", 0)
            }
            startActivity(intent)
        }
        
        // Navegación por click mantenida para compatibilidad
        findViewById<androidx.cardview.widget.CardView>(R.id.cardCardinalsContainer).setOnClickListener {
            navigateToTeam(1) // Cardinals
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.cardRamsContainer).setOnClickListener {
            navigateToTeam(2) // Rams
        }
        
        findViewById<androidx.cardview.widget.CardView>(R.id.cardSeahawksContainer).setOnClickListener {
            navigateToTeam(3) // Seahawks
        }
    }

    // Métodos requeridos por GestureDetector.OnGestureListener
    override fun onDown(e: MotionEvent): Boolean = true
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean = false
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean = false
    override fun onLongPress(e: MotionEvent) {}
    
    // Clase de datos para equipos
    data class TeamData(
        val name: String,
        val color: String,
        val backgroundImage: String,
        val activityClass: Class<*>
    )
}