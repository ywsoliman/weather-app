package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import com.github.matteobattilana.weather.PrecipType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class WeatherAnimationViewModel: ViewModel() {

    private val _weatherState = MutableStateFlow(PrecipType.CLEAR)
    val weatherState = _weatherState.asStateFlow()

    fun setWeatherState(state: PrecipType) {
        _weatherState.value = state
    }

}