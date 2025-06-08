package com.binbean.register

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.binbean.admin.model.PhotoItem
import com.binbean.register.databinding.AddPhotoFooterBinding
import com.binbean.register.databinding.AddPhotoItemBinding
import com.bumptech.glide.Glide

class PhotoAdapter(
    private val onClick: () -> Unit,
    private val onDelete: (Int) -> Unit
) : ListAdapter<PhotoItem, RecyclerView.ViewHolder>(diffCallback) {

    override fun getItemCount(): Int = currentList.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == currentList.size) TYPE_FOOTER else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_FOOTER) {
            val binding = AddPhotoFooterBinding.inflate(inflater, parent, false)
            FooterViewHolder(binding)
        } else {
            val binding = AddPhotoItemBinding.inflate(inflater, parent, false)
            ItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder && position < currentList.size) {
            holder.bind(getItem(position))
        } else if (holder is FooterViewHolder) {
            holder.itemView.setOnClickListener { onClick() }
        }
    }

    inner class ItemViewHolder(private val binding: AddPhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PhotoItem) {
            val imageSource = when (item) {
                is PhotoItem.Local -> item.uri
                is PhotoItem.Remote -> item.url
            }

            Glide.with(binding.photo.context)
                .load(imageSource)
                .centerCrop()
                .into(binding.photo)

            binding.delBtn.setOnClickListener {
                onDelete(bindingAdapterPosition)
            }
        }
    }

    inner class FooterViewHolder(binding: AddPhotoFooterBinding) :
        RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_FOOTER = 1

        private val diffCallback = object : DiffUtil.ItemCallback<PhotoItem>() {
            override fun areItemsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: PhotoItem, newItem: PhotoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
