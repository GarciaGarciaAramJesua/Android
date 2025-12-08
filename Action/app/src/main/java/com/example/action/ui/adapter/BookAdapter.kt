package com.example.action.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.action.R
import com.example.action.data.remote.model.OpenLibraryBook
import com.example.action.databinding.ItemBookBinding

class BookAdapter(
    private val onBookClick: (OpenLibraryBook) -> Unit,
    private val onFavoriteClick: (OpenLibraryBook) -> Unit
) : ListAdapter<OpenLibraryBook, BookAdapter.BookViewHolder>(BookDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class BookViewHolder(private val binding: ItemBookBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: OpenLibraryBook) {
            binding.apply {
                tvTitle.text = book.title
                tvAuthor.text = book.getAuthor() ?: "Autor desconocido"
                tvYear.text = book.firstPublishYear?.toString() ?: ""
                
                Glide.with(itemView.context)
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivCover)
                
                root.setOnClickListener { onBookClick(book) }
                btnFavorite.setOnClickListener { onFavoriteClick(book) }
            }
        }
    }
    
    class BookDiffCallback : DiffUtil.ItemCallback<OpenLibraryBook>() {
        override fun areItemsTheSame(oldItem: OpenLibraryBook, newItem: OpenLibraryBook): Boolean {
            return oldItem.key == newItem.key
        }
        
        override fun areContentsTheSame(oldItem: OpenLibraryBook, newItem: OpenLibraryBook): Boolean {
            return oldItem == newItem
        }
    }
}
