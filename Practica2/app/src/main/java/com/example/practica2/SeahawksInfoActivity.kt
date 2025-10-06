package com.example.practica2

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class SeahawksInfoActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seahawks_info)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // Recibir datos del equipo
        val teamName = intent.getStringExtra("TEAM_NAME") ?: "Seattle Seahawks"
        val teamColor = intent.getStringExtra("TEAM_COLOR") ?: "#002244"
        val teamBackground = intent.getStringExtra("TEAM_BACKGROUND") ?: "seahawks_image"
        
        setupViewPager()
        setupToolbar(teamName, teamBackground)
    }
    
    private fun setupViewPager() {
        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)
        
        val adapter = SeahawksInfoPagerAdapter(this)
        viewPager.adapter = adapter
        
        // Conectar TabLayout con ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Overview"
                1 -> "Players" 
                2 -> "Statistics"
                else -> "Tab ${position + 1}"
            }
            // Personalizar tabs
            tab.view.background = resources.getDrawable(R.drawable.tab_selector, theme)
        }.attach()
    }
    
    private fun setupToolbar(teamName: String, teamBackground: String) {
        // Configurar imagen y títulos dinámicos
        val ivTeamImage = findViewById<ImageView>(R.id.ivSeahawksImage)
        val tvTeamTitle = findViewById<TextView>(R.id.tvSeahawksTitle)
        val tvTeamSubtitle = findViewById<TextView>(R.id.tvSeahawksSubtitle)

        // Configurar imagen de fondo
        val imageResourceId = resources.getIdentifier(teamBackground, "drawable", packageName)
        if (imageResourceId != 0) {
            ivTeamImage.setImageResource(imageResourceId)
        }
        
        // Configurar títulos
        tvTeamTitle.text = "Seattle"
        tvTeamSubtitle.text = "Seahawks"
        
        supportActionBar?.apply {
            title = teamName
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    
    // Adapter para ViewPager2 con Fragments
    private inner class SeahawksInfoPagerAdapter(fragmentActivity: FragmentActivity) :      
        FragmentStateAdapter(fragmentActivity) {
        
        override fun getItemCount(): Int = 3
        
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> OverviewFragment.newInstance("Seattle Seahawks")
                1 -> PlayersFragment.newInstance("Seattle Seahawks")
                2 -> StatisticsFragment.newInstance("Seattle Seahawks")
                else -> OverviewFragment.newInstance("Seattle Seahawks")
            }
        }
    }
}