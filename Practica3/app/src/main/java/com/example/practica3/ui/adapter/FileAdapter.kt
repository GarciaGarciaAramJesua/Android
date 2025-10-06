package com.example.practica3.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.practica3.R
import com.example.practica3.data.model.FileItem
import com.example.practica3.data.model.ViewType
import com.example.practica3.databinding.ItemFileGridBinding
import com.example.practica3.databinding.ItemFileListBinding

class FileAdapter(
    private var viewType: ViewType = ViewType.LIST,
    private val onFileClick: (FileItem) -> Unit,
    private val onFileLongClick: (FileItem) -> Unit
) : ListAdapter<FileItem, RecyclerView.ViewHolder>(FileItemDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_LIST = 0
        private const val VIEW_TYPE_GRID = 1
    }

    fun updateViewType(newViewType: ViewType) {
        viewType = newViewType
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewType) {
            ViewType.LIST -> VIEW_TYPE_LIST
            ViewType.GRID -> VIEW_TYPE_GRID
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_GRID -> {
                val binding = ItemFileGridBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                GridViewHolder(binding)
            }
            else -> {
                val binding = ItemFileListBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ListViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val fileItem = getItem(position)
        when (holder) {
            is ListViewHolder -> holder.bind(fileItem)
            is GridViewHolder -> holder.bind(fileItem)
        }
    }

    inner class ListViewHolder(
        private val binding: ItemFileListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fileItem: FileItem) {
            binding.apply {
                textFileName.text = fileItem.name
                textFileInfo.text = if (fileItem.isDirectory) {
                    root.context.getString(R.string.folder_items, 0) // TODO: Count items
                } else {
                    "${fileItem.getFormattedSize()} • ${fileItem.getFormattedDate()}"
                }

                // Set file icon
                if (fileItem.isImageFile() && !fileItem.isDirectory) {
                    Glide.with(root.context)
                        .load(fileItem.path)
                        .placeholder(fileItem.getFileTypeIcon())
                        .error(fileItem.getFileTypeIcon())
                        .centerCrop()
                        .into(imageFileIcon)
                } else {
                    imageFileIcon.setImageResource(fileItem.getFileTypeIcon())
                }

                // Set click listeners
                root.setOnClickListener { onFileClick(fileItem) }
                root.setOnLongClickListener { 
                    onFileLongClick(fileItem)
                    true
                }

                // Show/hide hidden file indicator
                if (fileItem.isHidden) {
                    textFileName.alpha = 0.6f
                    textFileInfo.alpha = 0.6f
                    imageFileIcon.alpha = 0.6f
                } else {
                    textFileName.alpha = 1.0f
                    textFileInfo.alpha = 1.0f
                    imageFileIcon.alpha = 1.0f
                }
            }
        }
    }

    inner class GridViewHolder(
        private val binding: ItemFileGridBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(fileItem: FileItem) {
            binding.apply {
                textFileName.text = fileItem.name

                // Set file icon or thumbnail
                if (fileItem.isImageFile() && !fileItem.isDirectory) {
                    Glide.with(root.context)
                        .load(fileItem.path)
                        .placeholder(fileItem.getFileTypeIcon())
                        .error(fileItem.getFileTypeIcon())
                        .centerCrop()
                        .into(imageFileIcon)
                } else {
                    imageFileIcon.setImageResource(fileItem.getFileTypeIcon())
                }

                // Set click listeners
                root.setOnClickListener { onFileClick(fileItem) }
                root.setOnLongClickListener { 
                    onFileLongClick(fileItem)
                    true
                }

                // Show/hide hidden file indicator
                if (fileItem.isHidden) {
                    textFileName.alpha = 0.6f
                    imageFileIcon.alpha = 0.6f
                } else {
                    textFileName.alpha = 1.0f
                    imageFileIcon.alpha = 1.0f
                }
            }
        }
    }

    class FileItemDiffCallback : DiffUtil.ItemCallback<FileItem>() {
        override fun areItemsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem.path == newItem.path
        }

        override fun areContentsTheSame(oldItem: FileItem, newItem: FileItem): Boolean {
            return oldItem == newItem
        }
    }
}