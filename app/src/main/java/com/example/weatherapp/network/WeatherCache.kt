package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherResponse

object WeatherCache {

    private val cachedWeather = mutableMapOf<String, WeatherResponse>()

    fun getCachedWeather(key: String): WeatherResponse? {
        return cachedWeather[key]
    }

    fun cacheWeather(key: String, weatherResponse: WeatherResponse) {
        cachedWeather[key] = weatherResponse
    }

}