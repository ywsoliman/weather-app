package com.example.weatherapp.models

import com.example.weatherapp.util.Constants

interface IRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ): CurrentWeatherResponse
}
