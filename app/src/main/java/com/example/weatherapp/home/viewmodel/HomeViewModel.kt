package com.example.weatherapp.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.CurrentWeatherResponse
import com.example.weatherapp.models.ForecastResponse
import com.example.weatherapp.models.Repository
import com.example.weatherapp.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private val _currentWeather = MutableLiveData<CurrentWeatherResponse>()
    val currentWeather: LiveData<CurrentWeatherResponse>
        get() = _currentWeather

    private val _currentForecast = MutableLiveData<List<ForecastResponse.Data>>()
    val currentForecast: LiveData<List<ForecastResponse.Data>>
        get() = _currentForecast

    private val _currentLocation = MutableLiveData<Pair<Double, Double>>()
    val currentLocation: LiveData<Pair<Double, Double>>
        get() = _currentLocation


    fun getCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentWeather.postValue(repo.getCurrentWeather(lat, lon, apiKey, units, lang))
        }
    }

    fun getForecastWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        units: String? = "standard",
        lang: String? = "en"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentForecast.postValue(repo.getForecastWeather(lat, lon, apiKey, units, lang).list)
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
        _currentLocation.postValue(Pair(latitude, longitude))
    }

}