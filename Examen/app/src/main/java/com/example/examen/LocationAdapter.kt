package com.example.examen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.examen.data.LocationEntity
import com.example.examen.databinding.ItemLocationBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LocationAdapter : ListAdapter<LocationEntity, LocationAdapter.LocationViewHolder>(LocationDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val binding = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LocationViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class LocationViewHolder(private val binding: ItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        
        fun bind(location: LocationEntity) {
            binding.tvDateTime.text = dateFormat.format(Date(location.timestamp))
            binding.tvCoordinates.text = "Lat: %.6f, Lon: %.6f".format(location.latitude, location.longitude)
            binding.tvItemAccuracy.text = "Precision: %.2f metros".format(location.accuracy)
        }
    }
    
    class LocationDiffCallback : DiffUtil.ItemCallback<LocationEntity>() {
        override fun areItemsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: LocationEntity, newItem: LocationEntity): Boolean {
            return oldItem == newItem
        }
    }
}
