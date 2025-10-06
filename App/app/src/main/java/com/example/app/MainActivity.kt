package com.example.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    // Referencias a los botones
    private lateinit var btnHome: Button
    private lateinit var btnSearch: Button
    private lateinit var btnNotifications: Button
    private lateinit var btnProfile: Button
    private lateinit var btnSettings: Button
    
    // Botones para navegar a otros activities
    private lateinit var btnGoToSecond: Button
    private lateinit var btnGoToThird: Button

    // Fragment actual seleccionado
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initializeViews()
        setupNavigation()
        setupActivityNavigation()
        
        // Mostrar HomeFragment por defecto
        if (savedInstanceState == null) {
            showFragment(HomeFragment.newInstance())
            updateButtonStates(btnHome)
        }
    }

    private fun initializeViews() {
        btnHome = findViewById(R.id.btn_home)
        btnSearch = findViewById(R.id.btn_search)
        btnNotifications = findViewById(R.id.btn_notifications)
        btnProfile = findViewById(R.id.btn_profile)
        btnSettings = findViewById(R.id.btn_settings)
        
        // Botones para otros activities
        btnGoToSecond = findViewById(R.id.btnGoToSecond)
        btnGoToThird = findViewById(R.id.btnGoToThird)
    }

    private fun setupNavigation() {
        btnHome.setOnClickListener {
            showFragment(HomeFragment.newInstance())
            updateButtonStates(btnHome)
        }

        btnSearch.setOnClickListener {
            showFragment(SearchFragment.newInstance())
            updateButtonStates(btnSearch)
        }

        btnNotifications.setOnClickListener {
            showFragment(NotificationsFragment.newInstance())
            updateButtonStates(btnNotifications)
        }

        btnProfile.setOnClickListener {
            showFragment(ProfileFragment.newInstance())
            updateButtonStates(btnProfile)
        }

        btnSettings.setOnClickListener {
            showFragment(SettingsFragment.newInstance())
            updateButtonStates(btnSettings)
        }
    }

    private fun setupActivityNavigation() {
        btnGoToSecond.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        btnGoToThird.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            intent.putExtra("userName", "Usuario Principal")
            startActivity(intent)
        }
    }

    private fun showFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun updateButtonStates(selectedButton: Button) {
        // Resetear todos los botones al estado no seleccionado
        val buttons = listOf(btnHome, btnSearch, btnNotifications, btnProfile, btnSettings)
        buttons.forEach { button ->
            button.backgroundTintList = getColorStateList(R.color.blue)
        }
        
        // Destacar el botón seleccionado
        selectedButton.backgroundTintList = getColorStateList(R.color.purple_500)
    }
}