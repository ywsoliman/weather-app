package com.example.weatherapp.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.WeatherDayItemBinding
import com.example.weatherapp.models.WeatherResponse

class NextDaysWeatherAdapter :
    ListAdapter<WeatherResponse.Daily, NextDaysWeatherAdapter.NextDaysViewHolder>(NextDaysDiffUtil()) {

    private lateinit var binding: WeatherDayItemBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NextDaysViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.weather_day_item, parent, false)
        return NextDaysViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NextDaysViewHolder,
        position: Int
    ) {
        val current = getItem(position)
        holder.binding.currentDay = current
    }

    class NextDaysViewHolder(val binding: WeatherDayItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    class NextDaysDiffUtil : DiffUtil.ItemCallback<WeatherResponse.Daily>() {
        override fun areItemsTheSame(
            oldItem: WeatherResponse.Daily,
            newItem: WeatherResponse.Daily
        ): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(
            oldItem: WeatherResponse.Daily,
            newItem: WeatherResponse.Daily
        ): Boolean {
            return oldItem == newItem
        }

    }

}