package com.example.weatherapp.network

import android.util.Log
import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.GeocodingResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    override suspend fun getGeocoding(
        lat: Double,
        lon: Double,
        apiKey: String
    ): Flow<GeocodingResponse> {
        return flow {
            val response = dao.getGeocoding(lat, lon)
            if (response.isSuccessful)
                response.body()?.let { emit(it) }
            else
                Log.i(TAG, "getGeocoding: WeatherRemoteDataSource -> FAILED")
        }
    }
}