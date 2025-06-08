package com.binbean.register.drawing

import android.content.ClipData
import android.view.LayoutInflater
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.binbean.domain.cafe.ObjectItem
import com.binbean.register.databinding.ItemObjectBinding

class ObjectListAdapter: ListAdapter<ObjectItem, ObjectListAdapter.ObjectViewHolder>(DiffCallback()) {
    inner class ObjectViewHolder(private val binding: ItemObjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ObjectItem) {
            binding.apply {
                itemLabel.text = item.label

                itemIcon.setImageResource(item.iconResId)
                itemIcon.setOnLongClickListener {
                    val clip = ClipData.newPlainText("type", item.type)
                    val shadow = DragShadowBuilder(binding.itemIcon)
                    it.startDragAndDrop(clip, shadow, it, 0)
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val binding = ItemObjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ObjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<ObjectItem>() {
        override fun areItemsTheSame(oldItem: ObjectItem, newItem: ObjectItem) = oldItem == newItem
        override fun areContentsTheSame(oldItem: ObjectItem, newItem: ObjectItem)= oldItem == newItem
    }
}