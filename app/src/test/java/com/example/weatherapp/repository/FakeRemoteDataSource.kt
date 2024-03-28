package com.example.weatherapp.repository

import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.IWeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRemoteDataSource: IWeatherRemoteDataSource {
    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        exclude: String,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse> {
        return flowOf(WeatherResponse(11.1, 22.2))
    }
}