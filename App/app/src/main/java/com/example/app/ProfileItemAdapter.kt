package com.example.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProfileItemAdapter(
    private val items: MutableList<ProfileItem>,
    private val onItemRemoved: (Int) -> Unit
) : RecyclerView.Adapter<ProfileItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvItemTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvItemDescription)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvItemTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvTitle.text = item.title
        holder.tvDescription.text = item.description
        holder.tvTimestamp.text = item.timestamp

        // Configurar click para eliminar
        holder.itemView.setOnLongClickListener {
            removeItem(position)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun addItem(item: ProfileItem) {
        items.add(item)
        notifyItemInserted(items.size - 1)
    }

    fun removeItem(position: Int) {
        if (position >= 0 && position < items.size) {
            items.removeAt(position)
            notifyItemRemoved(position)
            onItemRemoved(position)
        }
    }

    fun clearAll() {
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }
}

data class ProfileItem(
    val title: String,
    val description: String,
    val timestamp: String
)