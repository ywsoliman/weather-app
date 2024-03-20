package com.example.weatherapp.network

import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.ForecastResponse

object WeatherCache {

    var currentWeather: CurrentWeatherResponse? = null
    var currentForecast: ForecastResponse? = null

}