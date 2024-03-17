package com.example.weatherapp.network

import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.GeocodingResponse
import com.example.weatherapp.util.Constants
import kotlinx.coroutines.flow.Flow

interface IWeatherRemoteDataSource {
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

    suspend fun getGeocoding(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<GeocodingResponse>
}