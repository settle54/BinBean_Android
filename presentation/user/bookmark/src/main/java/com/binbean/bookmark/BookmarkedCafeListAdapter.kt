package com.binbean.bookmark

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.binbean.domain.FavoriteCafeResponse

class BookmarkedCafeListAdapter: ListAdapter<FavoriteCafeResponse, BookmarkedCafeListAdapter.BookmarkedCafeViewHolder>(DiffCallback()) {

    inner class BookmarkedCafeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.tv_cafe_name)
        private val seatRecyclerView: RecyclerView = view.findViewById(R.id.rv_seats)

        fun bind(item: FavoriteCafeResponse) {
            name.text = item.cafeName

            val seatAdapter = SeatListAdapter()
            seatRecyclerView.adapter = seatAdapter
            seatAdapter.submitList(item.seatsList)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkedCafeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cafe, parent, false)
        return BookmarkedCafeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkedCafeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<FavoriteCafeResponse>() {
        override fun areItemsTheSame(old: FavoriteCafeResponse, new: FavoriteCafeResponse) = old.cafeId == new.cafeId
        override fun areContentsTheSame(old: FavoriteCafeResponse, new: FavoriteCafeResponse) = old == new
    }
}