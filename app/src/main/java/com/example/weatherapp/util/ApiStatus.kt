package com.example.weatherapp.util

import com.example.weatherapp.models.WeatherResponse

sealed class ApiStatus {
    class Success(val response: WeatherResponse) : ApiStatus()
    class Failure(val throwable: Throwable) : ApiStatus()
    data object Loading : ApiStatus()
}