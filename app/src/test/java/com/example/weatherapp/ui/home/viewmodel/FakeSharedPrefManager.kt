package com.example.weatherapp.ui.home.viewmodel

import com.example.weatherapp.sharedpref.ISharedPrefManager
import com.google.android.gms.maps.model.LatLng

class FakeSharedPrefManager: ISharedPrefManager {
    override fun setCoordinates(lat: Double, lon: Double) {
        TODO("Not yet implemented")
    }

    override fun getCoordinates(): LatLng? {
        TODO("Not yet implemented")
    }

    override fun setLocationSettings(location: String) {
        TODO("Not yet implemented")
    }

    override fun getLocation(): String {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String {
        return "en"
    }

    override fun getTemperatureUnit(): String {
        TODO("Not yet implemented")
    }

    override fun getWindSpeedUnit(): String {
        TODO("Not yet implemented")
    }
}