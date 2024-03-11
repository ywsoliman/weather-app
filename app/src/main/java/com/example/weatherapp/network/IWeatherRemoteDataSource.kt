package com.example.weatherapp.network

import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.util.Constants

interface IWeatherRemoteDataSource {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ): CurrentWeatherResponse
}