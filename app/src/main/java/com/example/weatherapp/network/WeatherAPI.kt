package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {

    @GET("data/3.0/onecall")
    suspend fun getWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = Constants.API_KEY,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String? = null,
        @Query("lang") lang: String? = null
    ): Response<WeatherResponse>

}