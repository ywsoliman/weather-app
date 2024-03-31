package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object WeatherRemoteDataSource : IWeatherRemoteDataSource {

    private val dao = APIClient.weatherAPI

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        exclude: String,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse> {
        return flow {
            val response = dao.getWeather(lat, lon, apiKey, exclude, units, lang)
            if (response.isSuccessful)
                response.body()?.let { emit(it) }
        }
    }

}