package com.example.weatherapp.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WeatherTimeItemBinding
import com.example.weatherapp.models.Hourly
import com.example.weatherapp.models.WeatherResponse

class HourlyWeatherAdapter :
    ListAdapter<Hourly, HourlyWeatherAdapter.TimeViewHolder>(TimeDiffUtil()) {

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

    class TimeDiffUtil : ItemCallback<Hourly>() {
        override fun areItemsTheSame(
            oldItem: Hourly,
            newItem: Hourly
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: Hourly,
            newItem: Hourly
        ): Boolean {
            return oldItem == newItem
        }
    }


}