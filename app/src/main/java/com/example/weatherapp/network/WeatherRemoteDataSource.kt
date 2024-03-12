package com.example.weatherapp.network

import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.ForecastResponse

object WeatherRemoteDataSource : IWeatherRemoteDataSource {

    private val dao = APIClient.weatherAPI

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String?,
        lang: String?
    ): CurrentWeatherResponse {
        return dao.getCurrentWeather(lat, lon, apiKey, units, lang)
    }

    override suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String?,
        lang: String?
    ): ForecastResponse {
        return dao.getForecastWeather(lat, lon, apiKey, units, lang)
    }
}