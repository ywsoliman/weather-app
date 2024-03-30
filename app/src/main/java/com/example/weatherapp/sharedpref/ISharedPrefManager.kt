package com.example.weatherapp.sharedpref

import com.google.android.gms.maps.model.LatLng

interface ISharedPrefManager {
    fun setCoordinates(lat: Double, lon: Double)
    fun getCoordinates(): LatLng?
    fun setLocationSettings(location: String)
    fun getLocation(): String
    fun getLanguage(): String
    fun getTemperatureUnit(): String
    fun getWindSpeedUnit(): String
}