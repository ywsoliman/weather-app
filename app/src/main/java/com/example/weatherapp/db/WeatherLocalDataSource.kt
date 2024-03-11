package com.example.weatherapp.db

import android.content.Context

class WeatherLocalDataSource(context: Context) : IWeatherLocalDataSource {

    companion object {

        @Volatile
        private var INSTANCE: WeatherLocalDataSource? = null

        fun getInstance(
            context: Context
        ): WeatherLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherLocalDataSource(context)
                INSTANCE = instance
                instance
            }
        }

    }

}