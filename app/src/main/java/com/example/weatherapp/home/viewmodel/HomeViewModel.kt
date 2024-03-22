package com.example.weatherapp.home.viewmodel

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.Repository
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.WeatherCache
import com.example.weatherapp.util.ApiStatus
import com.example.weatherapp.util.LocationStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(private val repo: Repository) : ViewModel() {

    private lateinit var sharedPreferences: SharedPreferences

    private val _locationStatus = MutableStateFlow<LocationStatus>(LocationStatus.Asking)
    val locationStatus = _locationStatus.asStateFlow()

    private val _apiStatus = MutableStateFlow<ApiStatus>(ApiStatus.Loading)
    val apiStatus = _apiStatus.asStateFlow()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    private fun getWeather(latitude: Double, longitude: Double) {

        val lat = latitude.toString()
        val lon = longitude.toString()
        val key = "$lat,$lon"
        val cachedWeather = WeatherCache.getCachedWeather(key)
        cachedWeather?.let {
            _weather.value = it
            _apiStatus.value = ApiStatus.Success(it)
            return
        }

        viewModelScope.launch {
            repo.getWeather(
                latitude,
                longitude,
                lang = sharedPreferences.getString("language", "en"),
                units = convertTemperatureToUnits()
            )
                .collect {
                    _weather.value = it
                    _apiStatus.value = ApiStatus.Success(it)
                    WeatherCache.cacheWeather(key, it)
                }
        }
    }

    private fun convertTemperatureToUnits() =
        if (sharedPreferences.getString("temperature", "celsius") == "celsius")
            "metric"
        else if (sharedPreferences.getString("temperature", "celsius") == "kelvin")
            "standard"
        else
            "imperial"

    fun getCurrentDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek =
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        return "$dayOfWeek, $dayOfMonth $month"
    }

    private fun locationGranted() {
        _locationStatus.value = LocationStatus.Granted
    }

    fun setLocationCoordinates(latitude: Double, longitude: Double) {
        locationGranted()
        getWeather(latitude, longitude)
    }

    fun locationDenied() {
        _locationStatus.value = LocationStatus.Denied
    }

    fun setSharedPreferences(sp: SharedPreferences?) {
        sp?.let { sharedPreferences = it }
    }
}