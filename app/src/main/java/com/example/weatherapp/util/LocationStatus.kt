package com.example.weatherapp.util

sealed class LocationStatus {
    data object Granted : LocationStatus()
    data object Denied : LocationStatus()
    data object Asking : LocationStatus()
}