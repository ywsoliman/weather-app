package com.example.weatherapp.cachemanager

import com.example.weatherapp.models.WeatherResponse

object WeatherCache {

    private val cachedWeather = mutableMapOf<Pair<String, String>, WeatherResponse>()

    fun getCachedWeather(key: Pair<String, String>): WeatherResponse? {
        return cachedWeather[key]
    }

    fun cacheWeather(key: Pair<String, String>, weatherResponse: WeatherResponse) {
        cachedWeather[key] = weatherResponse
    }
}