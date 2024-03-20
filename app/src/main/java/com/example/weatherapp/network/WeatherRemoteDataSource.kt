package com.example.weatherapp.network

import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.ForecastResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response

private const val TAG = "WeatherRemoteDataSource"

object WeatherRemoteDataSource : IWeatherRemoteDataSource {

    private val dao = APIClient.weatherAPI

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String?,
        lang: String?
    ): Flow<CurrentWeatherResponse> {
        return flow {
            val response = dao.getCurrentWeather(lat, lon, apiKey, units, lang)
            if (response.isSuccessful)
                response.body()?.let { emit(it) }
        }
    }

    override suspend fun getForecastWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        units: String?,
        lang: String?
    ): Flow<ForecastResponse> {
        return flow {
            val response = dao.getForecastWeather(lat, lon, apiKey, units, lang)
            if (response.isSuccessful)
                response.body()?.let { emit(it) }
        }
    }

}