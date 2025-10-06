package com.example.practica2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PlayersFragment : Fragment() {
    
    private lateinit var teamName: String
    private lateinit var recyclerView: RecyclerView
    
    companion object {
        private const val ARG_TEAM_NAME = "team_name"
        
        fun newInstance(teamName: String): PlayersFragment {
            val fragment = PlayersFragment()
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
        return inflater.inflate(R.layout.fragment_players, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupPlayersContent(view)
    }
    
    private fun setupPlayersContent(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewPlayers)
        recyclerView.layoutManager = LinearLayoutManager(context)
        
        val players = getTeamPlayers(teamName)
        val adapter = PlayersAdapter(players)
        recyclerView.adapter = adapter
    }
    
    private fun getTeamPlayers(teamName: String): List<Player> {
        return when (teamName) {
            "San Francisco 49ers" -> listOf(
                Player("Brock Purdy", "QB", "#13"),
                Player("Christian McCaffrey", "RB", "#23"),
                Player("Deebo Samuel", "WR", "#19"),
                Player("George Kittle", "TE", "#85"),
                Player("Nick Bosa", "DE", "#97")
            )
            "Arizona Cardinals" -> listOf(
                Player("Kyler Murray", "QB", "#1"),
                Player("James Conner", "RB", "#6"),
                Player("DeAndre Hopkins", "WR", "#10"),
                Player("Zach Ertz", "TE", "#86"),
                Player("J.J. Watt", "DE", "#99")
            )
            "Los Angeles Rams" -> listOf(
                Player("Matthew Stafford", "QB", "#9"),
                Player("Cam Akers", "RB", "#23"),
                Player("Cooper Kupp", "WR", "#10"),
                Player("Tyler Higbee", "TE", "#89"),
                Player("Aaron Donald", "DT", "#99")
            )
            "Seattle Seahawks" -> listOf(
                Player("Geno Smith", "QB", "#7"),
                Player("Kenneth Walker III", "RB", "#9"),
                Player("DK Metcalf", "WR", "#14"),
                Player("Tyler Lockett", "WR", "#16"),
                Player("Bobby Wagner", "LB", "#54")
            )
            else -> emptyList()
        }
    }
    
    // Data class para jugadores
    data class Player(
        val name: String,
        val position: String,
        val number: String
    )
    
    // Adapter simple para RecyclerView
    private inner class PlayersAdapter(private val players: List<Player>) : 
        RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_player, parent, false)
            return PlayerViewHolder(view)
        }
        
        override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
            holder.bind(players[position])
        }
        
        override fun getItemCount(): Int = players.size
        
        inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val tvPlayerName: TextView = itemView.findViewById(R.id.tvPlayerName)
            private val tvPlayerPosition: TextView = itemView.findViewById(R.id.tvPlayerPosition)
            private val tvPlayerNumber: TextView = itemView.findViewById(R.id.tvPlayerNumber)
            
            fun bind(player: Player) {
                tvPlayerName.text = player.name
                tvPlayerPosition.text = player.position
                tvPlayerNumber.text = player.number
            }
        }
    }
}