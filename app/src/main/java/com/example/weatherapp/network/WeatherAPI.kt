package com.example.weatherapp.network

import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String? = null,
        @Query("lang") lang: String? = null
    ): Response<CurrentWeatherResponse>

    @GET("data/2.5/forecast")
    suspend fun getForecastWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("units") units: String? = null,
        @Query("lang") lang: String? = null
    ): Response<ForecastResponse>

    @GET("geo/1.0/reverse")
    suspend fun getGeocoding(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY
    ): Response<FavoritePlaceDTO>

}