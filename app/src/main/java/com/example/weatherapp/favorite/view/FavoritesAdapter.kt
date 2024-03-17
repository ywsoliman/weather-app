package com.example.weatherapp.favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.GeocodingResponseItem

class FavoritesAdapter(
    private val context: Context,
    private val onClickListener: (GeocodingResponseItem) -> Unit,
    private val deleteListener: (GeocodingResponseItem) -> Unit
) :
    ListAdapter<GeocodingResponseItem, FavoritesAdapter.FavoritesViewHolder>(FavoritesDiffUtil()) {

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
        val settingsPref = PreferenceManager.getDefaultSharedPreferences(context)
        val placeString: String = if (settingsPref.getString("language", "en") == "en")
            "${current.localNames?.en}, ${current.country}"
        else
            "${current.localNames?.ar}، ${current.country}"
        holder.placeName.text = placeString
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

    class FavoritesDiffUtil : DiffUtil.ItemCallback<GeocodingResponseItem>() {
        override fun areItemsTheSame(
            oldItem: GeocodingResponseItem,
            newItem: GeocodingResponseItem
        ): Boolean {
            return oldItem.lat == newItem.lat && oldItem.lon == newItem.lon
        }

        override fun areContentsTheSame(
            oldItem: GeocodingResponseItem,
            newItem: GeocodingResponseItem
        ): Boolean {
            return oldItem == newItem
        }

    }


}