package com.example.weatherapp.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng

class SharedPrefManager private constructor(context: Context) {

    private val sharedPref =
        context.getSharedPreferences(Constants.LATLNG_PREF, Context.MODE_PRIVATE)

    private val settingsPref =
        PreferenceManager.getDefaultSharedPreferences(context)


    companion object {

        @Volatile
        private var INSTANCE: SharedPrefManager? = null

        fun getInstance(context: Context): SharedPrefManager {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPrefManager(context)
                INSTANCE = instance
                instance
            }
        }

    }

    fun setCoordinates(lat: Double, lon: Double) {
        sharedPref
            .edit()
            .putString(Constants.LATITUDE, lat.toString())
            .putString(Constants.LONGITUDE, lon.toString())
            .apply()
    }

    fun getCoordinates(): LatLng? {
        val lat = sharedPref.getString(Constants.LATITUDE, "") ?: ""
        val lon = sharedPref.getString(Constants.LONGITUDE, "") ?: ""
        return if (lat.isNotEmpty() && lon.isNotEmpty())
            LatLng(lat.toDouble(), lon.toDouble())
        else
            null
    }

    fun setLocationSettings(location: String) {
        settingsPref
            .edit()
            .putString("location", location)
            .apply()
    }

    fun getLanguage(): String {
        return settingsPref.getString("language", "en") ?: "en"
    }

    fun getTemperatureUnit(): String {
        return settingsPref.getString("temperature", "kelvin") ?: "kelvin"
    }

    fun getWindSpeedUnit(): String {
        return settingsPref.getString("wind", "m/s") ?: "m/s"
    }

    fun convertTemperatureToUnits() = when (getTemperatureUnit()) {
        "celsius" -> "metric"
        "kelvin" -> "standard"
        else -> "imperial"
    }

}