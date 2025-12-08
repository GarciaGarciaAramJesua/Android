package com.example.action.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.action.R
import com.example.action.data.local.entity.FavoriteEntity
import com.example.action.databinding.ItemFavoriteBinding

class FavoriteAdapter(
    private val onRemoveClick: (FavoriteEntity) -> Unit
) : ListAdapter<FavoriteEntity, FavoriteAdapter.FavoriteViewHolder>(FavoriteDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class FavoriteViewHolder(private val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: FavoriteEntity) {
            binding.apply {
                tvTitle.text = favorite.title
                tvAuthor.text = favorite.author ?: "Autor desconocido"
                
                Glide.with(itemView.context)
                    .load(favorite.coverUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivCover)
                
                btnRemove.setOnClickListener { onRemoveClick(favorite) }
            }
        }
    }
    
    class FavoriteDiffCallback : DiffUtil.ItemCallback<FavoriteEntity>() {
        override fun areItemsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: FavoriteEntity, newItem: FavoriteEntity): Boolean {
            return oldItem == newItem
        }
    }
}
