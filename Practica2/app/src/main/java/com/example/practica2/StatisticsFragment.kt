package com.example.practica2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StatisticsFragment : Fragment() {
    
    private lateinit var teamName: String
    
    companion object {
        private const val ARG_TEAM_NAME = "team_name"
        
        fun newInstance(teamName: String): StatisticsFragment {
            val fragment = StatisticsFragment()
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
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupStatisticsContent(view)
    }
    
    private fun setupStatisticsContent(view: View) {
        val tvSeasonRecord = view.findViewById<TextView>(R.id.tvSeasonRecord)
        val tvOffenseRank = view.findViewById<TextView>(R.id.tvOffenseRank)
        val tvDefenseRank = view.findViewById<TextView>(R.id.tvDefenseRank)
        val tvPointsScored = view.findViewById<TextView>(R.id.tvPointsScored)
        val tvPointsAllowed = view.findViewById<TextView>(R.id.tvPointsAllowed)
        val tvTotalYards = view.findViewById<TextView>(R.id.tvTotalYards)
        
        when (teamName) {
            "San Francisco 49ers" -> {
                tvSeasonRecord.text = "2023 Record: 12-5"
                tvOffenseRank.text = "Offense Rank: #5"
                tvDefenseRank.text = "Defense Rank: #3"
                tvPointsScored.text = "Points Scored: 28.9 per game"
                tvPointsAllowed.text = "Points Allowed: 17.5 per game"
                tvTotalYards.text = "Total Yards: 379.4 per game"
            }
            "Arizona Cardinals" -> {
                tvSeasonRecord.text = "2023 Record: 4-13"
                tvOffenseRank.text = "Offense Rank: #24"
                tvDefenseRank.text = "Defense Rank: #28"
                tvPointsScored.text = "Points Scored: 18.5 per game"
                tvPointsAllowed.text = "Points Allowed: 28.3 per game"
                tvTotalYards.text = "Total Yards: 298.2 per game"
            }
            "Los Angeles Rams" -> {
                tvSeasonRecord.text = "2023 Record: 10-7"
                tvOffenseRank.text = "Offense Rank: #12"
                tvDefenseRank.text = "Defense Rank: #15"
                tvPointsScored.text = "Points Scored: 23.1 per game"
                tvPointsAllowed.text = "Points Allowed: 21.8 per game"
                tvTotalYards.text = "Total Yards: 342.7 per game"
            }
            "Seattle Seahawks" -> {
                tvSeasonRecord.text = "2023 Record: 9-8"
                tvOffenseRank.text = "Offense Rank: #8"
                tvDefenseRank.text = "Defense Rank: #25"
                tvPointsScored.text = "Points Scored: 24.8 per game"
                tvPointsAllowed.text = "Points Allowed: 24.9 per game"
                tvTotalYards.text = "Total Yards: 356.1 per game"
            }
        }
    }
}