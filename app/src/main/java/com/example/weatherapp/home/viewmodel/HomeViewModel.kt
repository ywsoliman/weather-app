package com.example.weatherapp.home.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

class HomeViewModel(private val repo: Repository) : ViewModel() {

    private val _locationStatus = MutableStateFlow<LocationStatus>(LocationStatus.Asking)
    val locationStatus = _locationStatus.asStateFlow()

    private val _apiStatus = MutableStateFlow<ApiStatus>(ApiStatus.Loading)
    val apiStatus = _apiStatus.asStateFlow()

    private val _currentWeather = MutableStateFlow<CurrentWeatherResponse?>(null)
    val currentWeather = _currentWeather.asStateFlow()

    private val _todayForecast = MutableStateFlow<List<ForecastResponse.Data>?>(null)
    val todayForecast = _todayForecast.asStateFlow()

    private val _nextDaysForecast = MutableStateFlow<List<ForecastResponse.Data>?>(null)
    val nextDaysForecast = _nextDaysForecast.asStateFlow()

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodayForecast(response: List<ForecastResponse.Data>) {
        viewModelScope.launch {
            response.apply {
                val currentDate = LocalDate.now()
                val currentDateString =
                    currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val groupByDate = this.filter {
                    currentDateString == it.dt_txt.substringBefore(" ")
                }
                _todayForecast.emit(groupByDate)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getNextDaysForecast(response: List<ForecastResponse.Data>) {
        viewModelScope.launch {
            response.apply {
                val currentDate = LocalDate.now()
                val currentDateString =
                    currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val restOfDays = this
                    .filter {
                        currentDateString != it.dt_txt.substringBefore(" ")
                    }.groupBy {
                        it.dt_txt.substringBefore(" ")
                    }
                val res = mutableListOf<ForecastResponse.Data>()
                restOfDays.forEach { (_, item) ->
                    val minTemp = item.minOf { it.main.temp_min }
                    val maxTemp = item.maxOf { it.main.temp_max }
                    val randomDay = item[Random.nextInt(item.size)]
                    randomDay.main.temp_min = minTemp
                    randomDay.main.temp_max = maxTemp
                    res.add(randomDay)
                }
                _nextDaysForecast.emit(res)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                    getTodayForecast(it.list)
                    getNextDaysForecast(it.list)
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

//    private fun checkPermissions(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            appContext,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager =
//            appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
//    }

//    @SuppressLint("MissingPermission")
//    fun getFreshLocation() {
//        fusedClient.requestLocationUpdates(
//            LocationRequest.Builder(1000).apply {
//                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
//            }.build(),
//            object : LocationCallback() {
//                override fun onLocationResult(locationResult: LocationResult) {
//                    super.onLocationResult(locationResult)
//                    val longitude = locationResult.lastLocation?.longitude
//                    val latitude = locationResult.lastLocation?.latitude
//                    if (latitude != null && longitude != null) {
//                        setLocationCoordinates(latitude, longitude)
//                    }
//                    fusedClient.removeLocationUpdates(this)
//                }
//            },
//            Looper.myLooper()
//        )
//    }

}