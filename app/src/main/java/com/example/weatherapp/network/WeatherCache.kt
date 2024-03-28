package com.example.weatherapp.network

import com.example.weatherapp.models.WeatherResponse

object WeatherCache {

    private val cachedWeather = mutableMapOf<Pair<String, String>, WeatherResponse>()
    private var homeCachedWeather: WeatherResponse? = null

    fun getCachedWeather(key: Pair<String, String>): WeatherResponse? {
        return cachedWeather[key]
    }

    fun cacheWeather(key: Pair<String, String>, weatherResponse: WeatherResponse) {
        cachedWeather[key] = weatherResponse
    }

    fun getMainResponse(): WeatherResponse? {
        return homeCachedWeather
    }

    fun setMainResponse(response: WeatherResponse) {
        homeCachedWeather = response
    }

}