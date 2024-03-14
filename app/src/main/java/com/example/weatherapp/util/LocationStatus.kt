package com.example.weatherapp.util

sealed class LocationStatus {
    class Granted(val latitude: Double, val longitude: Double) : LocationStatus()
    data object Denied : LocationStatus()
    data object Asking : LocationStatus()
}