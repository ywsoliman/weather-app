package com.example.weatherapp.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.cachemanager.WeatherCache
import com.example.weatherapp.connectivitymanager.IConnectivityRepository
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.repository.IRepository
import com.example.weatherapp.sharedpref.ISharedPrefManager
import com.example.weatherapp.util.ApiStatus
import com.github.matteobattilana.weather.PrecipType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeViewModel(
    private val sharedPrefManager: ISharedPrefManager,
    connectivityRepository: IConnectivityRepository,
    private val repo: IRepository
) :
    ViewModel() {

    private val _apiStatus = MutableStateFlow<ApiStatus>(ApiStatus.Loading)
    val apiStatus = _apiStatus.asStateFlow()

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather = _weather.asStateFlow()

    private val _weatherState = MutableStateFlow(PrecipType.CLEAR)
    val weatherState = _weatherState.asStateFlow()

    val isConnected = connectivityRepository.isConnected

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCurrentTime(): Flow<String> = _weather.flatMapLatest { weatherResponse ->
        if (weatherResponse != null) {
            val locale = Locale(sharedPrefManager.getLanguage())
            val formatter = DateTimeFormatter.ofPattern("EEEE, d MMM HH:mm:ss", locale)
            val zoneId = ZoneId.of(weatherResponse.timezone)
            flow {
                while (true) {
                    val currentTime = LocalDateTime.now(zoneId).format(formatter)
                    emit(currentTime)
                    delay(1000)
                }
            }
        } else {
            flow {
                emit("00:00:00")
            }
        }
    }

    fun setLocationCoordinates(latitude: Double, longitude: Double, isMainResponse: Boolean) {

        viewModelScope.launch(Dispatchers.IO) {
            isConnected.collectLatest { isOnline ->

                if (!isOnline) {
                    repo.getMainResponse().flowOn(Dispatchers.IO)
                        .catch { _apiStatus.value = ApiStatus.Failure(it) }
                        .collectLatest { it?.let { processWeatherData(it) } }
                } else {

                    val lat = latitude.toString()
                    val lon = longitude.toString()
                    val key = Pair("$lat,$lon", sharedPrefManager.getLanguage())
                    val cachedWeather = WeatherCache.getCachedWeather(key)
                    cachedWeather?.let {
                        processWeatherData(it)
                        return@collectLatest
                    }

                    repo.getWeather(
                        latitude,
                        longitude,
                        lang = sharedPrefManager.getLanguage(),
                    ).flowOn(Dispatchers.IO)
                        .catch { _apiStatus.value = ApiStatus.Failure(it) }
                        .collectLatest {
                            processWeatherData(it)
                            WeatherCache.cacheWeather(key, it)
                            if (isMainResponse)
                                refreshMainResponse(it)
                        }
                }
            }
        }
    }

    private suspend fun refreshMainResponse(response: WeatherResponse) {
        repo.deleteOldResponse()
        repo.insertMainResponse(response)
    }

    private fun processWeatherData(it: WeatherResponse) {
        _weather.value = it
        _apiStatus.value = ApiStatus.Success(it)
        mapWeatherIcon()
    }

    private fun mapWeatherIcon() {
        _weather.value?.let {
            it.current?.weather?.get(0)?.icon?.let(::mapIconToWeatherState)
        }
    }


    private fun mapIconToWeatherState(icon: String) {
        when (icon) {
            "09d", "09n", "10d", "10n" -> _weatherState.value = PrecipType.RAIN
            "13d", "13n" -> _weatherState.value = PrecipType.SNOW
            else -> _weatherState.value = PrecipType.CLEAR
        }
    }
}