package com.example.weatherapp.favorite.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.FavoritePlaceDTO

class FavoritesAdapter(
    private val onClickListener: (FavoritePlaceDTO) -> Unit,
    private val deleteListener: (FavoritePlaceDTO) -> Unit
) :
    ListAdapter<FavoritePlaceDTO, FavoritesAdapter.FavoritesViewHolder>(FavoritesDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoritesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_favorite, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val current = getItem(position)
        holder.placeName.text = current.toString()
    }

    inner class FavoritesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onClickListener(getItem(adapterPosition))
            }
            itemView.findViewById<ImageButton>(R.id.deleteBtn).setOnClickListener {
                deleteListener(getItem(adapterPosition))
            }
        }

        val placeName: TextView = itemView.findViewById(R.id.placeNameText)
    }

    class FavoritesDiffUtil : DiffUtil.ItemCallback<FavoritePlaceDTO>() {
        override fun areItemsTheSame(
            oldItem: FavoritePlaceDTO,
            newItem: FavoritePlaceDTO
        ): Boolean {
            return oldItem.latitude == newItem.latitude && oldItem.longitude == newItem.longitude
        }

        override fun areContentsTheSame(
            oldItem: FavoritePlaceDTO,
            newItem: FavoritePlaceDTO
        ): Boolean {
            return oldItem == newItem
        }

    }


}