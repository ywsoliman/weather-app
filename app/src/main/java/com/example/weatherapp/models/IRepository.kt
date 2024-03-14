package com.example.weatherapp.models

import com.example.weatherapp.util.Constants
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ): Flow<CurrentWeatherResponse>

    suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ): Flow<ForecastResponse>
}