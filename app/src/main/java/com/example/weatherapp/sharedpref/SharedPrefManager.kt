package com.example.weatherapp.sharedpref

import android.content.Context
import androidx.preference.PreferenceManager
import com.example.weatherapp.util.Constants
import com.google.android.gms.maps.model.LatLng

class SharedPrefManager private constructor(context: Context) : ISharedPrefManager {

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

    override fun setCoordinates(lat: Double, lon: Double) {
        sharedPref
            .edit()
            .putString(Constants.LATITUDE, lat.toString())
            .putString(Constants.LONGITUDE, lon.toString())
            .apply()
    }

    override fun getCoordinates(): LatLng? {
        val lat = sharedPref.getString(Constants.LATITUDE, "") ?: ""
        val lon = sharedPref.getString(Constants.LONGITUDE, "") ?: ""
        return if (lat.isNotEmpty() && lon.isNotEmpty())
            LatLng(lat.toDouble(), lon.toDouble())
        else
            null
    }

    override fun setLocationSettings(location: String) {
        settingsPref
            .edit()
            .putString("location", location)
            .apply()
    }

    override fun getLocation() = settingsPref.getString("location", "gps") ?: "gps"

    override fun getLanguage() = settingsPref.getString("language", "en") ?: "en"

    override fun getTemperatureUnit() = settingsPref.getString("temperature", "kelvin") ?: "kelvin"

    override fun getWindSpeedUnit() = settingsPref.getString("wind", "m/s") ?: "m/s"

}