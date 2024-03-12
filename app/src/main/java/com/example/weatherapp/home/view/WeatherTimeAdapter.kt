package com.example.weatherapp.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WeatherTimeItemBinding
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.models.ForecastResponse

class WeatherTimeAdapter(private val homeViewModel: HomeViewModel) :
    ListAdapter<ForecastResponse.Data, WeatherTimeAdapter.TimeViewHolder>(TimeDiffUtil()) {

    private lateinit var binding: WeatherTimeItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.weather_time_item, parent, false)
        return TimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        val currentTime = getItem(position)
        holder.binding.currentTime = currentTime
    }

    class TimeViewHolder(val binding: WeatherTimeItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class TimeDiffUtil : ItemCallback<ForecastResponse.Data>() {
        override fun areItemsTheSame(
            oldItem: ForecastResponse.Data,
            newItem: ForecastResponse.Data
        ): Boolean {
            return oldItem.dt_txt == newItem.dt_txt
        }

        override fun areContentsTheSame(
            oldItem: ForecastResponse.Data,
            newItem: ForecastResponse.Data
        ): Boolean {
            return oldItem == newItem
        }
    }


}