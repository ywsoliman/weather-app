package com.example.weatherapp.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.Repository
import com.example.weatherapp.util.ApiStatus
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.LocationStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private val _locationStatus = MutableStateFlow<LocationStatus>(LocationStatus.Asking)
    val locationStatus = _locationStatus.asStateFlow()

    private val _apiStatus = MutableStateFlow<ApiStatus>(ApiStatus.Loading)
    val apiStatus = _apiStatus.asStateFlow()

    private val _currentWeather = MutableStateFlow<CurrentWeatherResponse?>(null)
    val currentWeather = _currentWeather.asStateFlow()

    private val _currentForecast = MutableStateFlow<ForecastResponse?>(null)
    val currentForecast = _currentForecast.asStateFlow()

    fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCurrentWeather(lat, lon, apiKey, units, lang)
                .catch {
                    _apiStatus.value = ApiStatus.Failure(it)
                }.collect {
                    _currentWeather.value = it
                    _apiStatus.value = ApiStatus.Success
                }
        }
    }

    fun getForecastWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ) {
        viewModelScope.launch {
            repo.getForecastWeather(lat, lon, apiKey, units, lang)
                .catch {
                    _apiStatus.value = ApiStatus.Failure(it)
                }.collect {
                    _currentForecast.value = it
                    _apiStatus.value = ApiStatus.Success
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
        _locationStatus.value = LocationStatus.Granted(latitude, longitude)
    }

    fun locationDenied() {
        _locationStatus.value = LocationStatus.Denied
    }

}