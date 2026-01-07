package com.example.examen

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examen.data.LocationDatabase
import com.example.examen.data.LocationRepository
import com.example.examen.databinding.ActivityHistoryBinding
import com.example.examen.utils.ThemeManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var repository: LocationRepository
    private lateinit var adapter: LocationAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.history_title)
        
        val database = LocationDatabase.getDatabase(applicationContext)
        repository = LocationRepository(database.locationDao())
        
        setupRecyclerView()
        observeLocations()
        
        binding.btnClearHistory.setOnClickListener {
            showClearConfirmationDialog()
        }
    }
    
    private fun setupRecyclerView() {
        adapter = LocationAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = this@HistoryActivity.adapter
        }
    }
    
    private fun observeLocations() {
        lifecycleScope.launch {
            repository.allLocations.collectLatest { locations ->
                if (locations.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.tvEmpty.visibility = View.VISIBLE
                    binding.tvLocationCount.text = getString(R.string.location_count, 0)
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvEmpty.visibility = View.GONE
                    binding.tvLocationCount.text = getString(R.string.location_count, locations.size)
                    adapter.submitList(locations)
                }
            }
        }
    }
    
    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.clear_history)
            .setMessage(R.string.confirm_clear)
            .setPositiveButton(R.string.yes) { _, _ ->
                clearHistory()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }
    
    private fun clearHistory() {
        lifecycleScope.launch {
            repository.deleteAll()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
