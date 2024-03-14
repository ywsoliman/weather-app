package com.example.weatherapp.util

sealed class ApiStatus {
    data object Success : ApiStatus()
    class Failure(val throwable: Throwable) : ApiStatus()
    data object Loading : ApiStatus()
}