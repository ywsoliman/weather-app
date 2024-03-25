package com.example.weatherapp.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.Repository
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.ConnectivityRepository
import com.example.weatherapp.network.WeatherCache
import com.example.weatherapp.util.ApiStatus
import com.example.weatherapp.util.SharedPrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

private const val TAG = "HomeViewModel"

class HomeViewModel(
    private val sharedPrefManager: SharedPrefManager,
    connectivityRepository: ConnectivityRepository,
    private val repo: Repository
) :
    ViewModel() {

    private val _apiStatus = MutableStateFlow<ApiStatus>(ApiStatus.Loading)
    val apiStatus = _apiStatus.asStateFlow()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    val isConnected = connectivityRepository.isConnected

    private fun getWeather(latitude: Double, longitude: Double) {

        viewModelScope.launch(Dispatchers.IO) {
            isConnected.collectLatest { isOnline ->

                if (!isOnline) {

                    repo.getMainResponse().collectLatest {
                        it?.let {
                            _weather.value = it
                            _apiStatus.value = ApiStatus.Success(it)
                        }
                    }

                } else {

                    val lat = latitude.toString()
                    val lon = longitude.toString()
                    val key = "$lat,$lon"
                    val cachedWeather = WeatherCache.getCachedWeather(key)
                    cachedWeather?.let {
                        _weather.value = it
                        _apiStatus.value = ApiStatus.Success(it)
                        return@collectLatest
                    }

                    repo.getWeather(
                        latitude,
                        longitude,
                        lang = sharedPrefManager.getLanguage(),
                        units = sharedPrefManager.convertTemperatureToUnits()
                    )
                        .collect {
                            _weather.value = it
                            _apiStatus.value = ApiStatus.Success(it)
                            WeatherCache.cacheWeather(key, it)
                        }

                }
            }
        }
    }

    fun getCurrentDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek =
            calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
        return "$dayOfWeek, $dayOfMonth $month"
    }

    fun setLocationCoordinates(latitude: Double, longitude: Double) {
        getWeather(latitude, longitude)
    }

}