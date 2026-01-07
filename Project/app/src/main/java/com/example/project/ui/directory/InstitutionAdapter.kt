package com.example.project.ui.directory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project.data.model.Institution
import com.example.project.databinding.ItemInstitutionBinding

class InstitutionAdapter(
    private val onCallClick: (Institution) -> Unit,
    private val onLocationClick: (Institution) -> Unit,
    private val onWebsiteClick: (Institution) -> Unit
) : ListAdapter<Institution, InstitutionAdapter.InstitutionViewHolder>(InstitutionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstitutionViewHolder {
        val binding = ItemInstitutionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InstitutionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstitutionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InstitutionViewHolder(
        private val binding: ItemInstitutionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(institution: Institution) {
            binding.nameTextView.text = institution.name
            binding.categoryTextView.text = getCategoryDisplayName(institution.category)
            binding.descriptionTextView.text = institution.description
            binding.addressTextView.text = institution.address
            binding.scheduleTextView.text = "Horario: ${institution.schedule}"

            binding.callButton.setOnClickListener {
                onCallClick(institution)
            }

            binding.locationButton.setOnClickListener {
                onLocationClick(institution)
            }

            binding.websiteButton.setOnClickListener {
                onWebsiteClick(institution)
            }
        }

        private fun getCategoryDisplayName(category: String): String {
            return when (category) {
                "seguridad" -> "Seguridad"
                "agua" -> "Agua"
                "género" -> "Género"
                "salud" -> "Salud"
                "educación" -> "Educación"
                "general" -> "General"
                else -> category
            }
        }
    }

    private class InstitutionDiffCallback : DiffUtil.ItemCallback<Institution>() {
        override fun areItemsTheSame(oldItem: Institution, newItem: Institution): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Institution, newItem: Institution): Boolean {
            return oldItem == newItem
        }
    }
}
