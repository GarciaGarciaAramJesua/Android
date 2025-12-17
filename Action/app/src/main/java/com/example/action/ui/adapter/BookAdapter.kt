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
    private val onFavoriteClick: (OpenLibraryBook) -> Unit,
    private val getFavoriteIds: () -> Set<String> = { emptySet() }
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
                tvAuthor.text = book.getAuthor() ?: "Unknown author"
                
                // Configurar año (ahora es un TextView)
                val year = book.firstPublishYear?.toString() ?: "N/A"
                tvYear.text = year
                
                Glide.with(itemView.context)
                    .load(book.coverUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(ivCover)
                
                // Actualizar icono según estado de favorito
                val isFavorite = getFavoriteIds().contains(book.getBookId())
                btnFavorite.setIconResource(
                    if (isFavorite) android.R.drawable.btn_star_big_on
                    else android.R.drawable.btn_star_big_off
                )
                btnFavorite.text = if (isFavorite) "Remove" else "Add to Library"
                
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
