package com.example.practica2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.content.Intent
import android.net.Uri
import android.widget.ImageView

class OverviewFragment : Fragment() {
    
    private lateinit var teamName: String
    
    companion object {
        private const val ARG_TEAM_NAME = "team_name"
        
        fun newInstance(teamName: String): OverviewFragment {
            val fragment = OverviewFragment()
            val args = Bundle().apply {
                putString(ARG_TEAM_NAME, teamName)
            }
            fragment.arguments = args
            return fragment
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            teamName = it.getString(ARG_TEAM_NAME) ?: "Unknown Team"
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupOverviewContent(view)
        setupVideoClick(view)
    }
    
    private fun setupVideoClick(view: View) {
        val ivTeamLogo = view.findViewById<ImageView>(R.id.ivTeamLogo)
    
        ivTeamLogo.setOnClickListener {
            val videoUrl = getTeamHighlightVideo(teamName)
            openVideo(videoUrl)
        }
    }

    private fun getTeamHighlightVideo(teamName: String): String {
        return when (teamName) {
            "San Francisco 49ers" -> "https://www.youtube.com/watch?v=mAD67jjXydE"
            "Arizona Cardinals" -> "https://www.youtube.com/watch?v=_nzquErK7mM" 
            "Los Angeles Rams" -> "https://www.youtube.com/watch?v=saiXqddvl7w"
            "Seattle Seahawks" -> "https://www.youtube.com/watch?v=4VX64Kw7pkY"
            else -> ""
        }
    }

    private fun openVideo(url: String) {
        try {
            // Opción 1: Abrir en la app de YouTube (si está instalada)
            val youtubeIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            youtubeIntent.setPackage("com.google.android.youtube")
            startActivity(youtubeIntent)
        } catch (e: Exception) {
            // Opción 2: Abrir en el navegador web
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(webIntent)
        }
    }

    private fun setupOverviewContent(view: View) {
    val tvTeamHistory = view.findViewById<TextView>(R.id.tvTeamHistory)
    val tvChampionships = view.findViewById<TextView>(R.id.tvChampionships)
    val tvStadium = view.findViewById<TextView>(R.id.tvStadium)
    val tvFounded = view.findViewById<TextView>(R.id.tvFounded)
    val colorPrimary = view.findViewById<View>(R.id.colorPrimary)
    val colorSecondary = view.findViewById<View>(R.id.colorSecondary)
    val tvColorText = view.findViewById<TextView>(R.id.tvColorText)
    val tvTeamLogo = view.findViewById<ImageView>(R.id.ivTeamLogo)
    
    when (teamName) {
        "San Francisco 49ers" -> {
            tvTeamHistory.text = "The San Francisco 49ers are one of the most successful franchises in NFL history, known for their innovative West Coast offense and championship tradition."
            tvChampionships.text = "Super Bowl Championships: 5 (1981, 1984, 1988, 1989, 1994)"
            tvStadium.text = "Levi's Stadium (Santa Clara, CA)"
            tvFounded.text = "1946"
            colorPrimary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#AA0000")))
            colorSecondary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFD700")))
            tvColorText.text = "Red & Gold"
            tvTeamLogo.setImageResource(R.drawable.logo_49ers)
        }
        "Arizona Cardinals" -> {
            tvTeamHistory.text = "The Arizona Cardinals are the oldest continuously run professional football team in the United States, founded in 1898."
            tvChampionships.text = "NFL Championships: 2 (1925, 1947)"
            tvStadium.text = "State Farm Stadium (Glendale, AZ)"
            tvFounded.text = "1898"
            colorPrimary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#97233F")))
            colorSecondary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFB612")))
            tvColorText.text = "Cardinal Red & Yellow"
            tvTeamLogo.setImageResource(R.drawable.logo_cardinals)  
        }
        "Los Angeles Rams" -> {
            tvTeamHistory.text = "The Los Angeles Rams have a rich history spanning multiple cities and have been known for their 'Greatest Show on Turf' offense."
            tvChampionships.text = "Super Bowl Championships: 2 (1999, 2021)"
            tvStadium.text = "SoFi Stadium (Los Angeles, CA)"
            tvFounded.text = "1936"
            colorPrimary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#003594")))
            colorSecondary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FFA300")))
            tvColorText.text = "Royal Blue & Gold"
            tvTeamLogo.setImageResource(R.drawable.logo_rams)
        }
        "Seattle Seahawks" -> {
            tvTeamHistory.text = "The Seattle Seahawks are known for their passionate '12th Man' fanbase and dominant Legion of Boom defense."
            tvChampionships.text = "Super Bowl Championships: 1 (2013)"
            tvStadium.text = "Lumen Field (Seattle, WA)"
            tvFounded.text = "1976"
            colorPrimary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#002244")))
            colorSecondary.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#69BE28")))
            tvColorText.text = "Navy & Action Green"
            tvTeamLogo.setImageResource(R.drawable.logo_seahawks)
        }
    }
}
}