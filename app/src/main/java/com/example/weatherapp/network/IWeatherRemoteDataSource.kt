package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        exclude: String,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse>

}