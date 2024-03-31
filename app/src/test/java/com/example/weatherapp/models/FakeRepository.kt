package com.example.weatherapp.models

import com.example.weatherapp.repository.IRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

class FakeRepository : IRepository {

    private val favoritePlaces = mutableListOf<FavoritePlaceDTO>()
    private val observableFavoritePlaces = MutableStateFlow<List<FavoritePlaceDTO>>(favoritePlaces)

    private val alarmAlerts = mutableListOf<AlarmItem>()
    private val observableAlarmAlerts = MutableStateFlow<List<AlarmItem>>(alarmAlerts)

    private var weatherResponse: WeatherResponse? = null

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        exclude: String,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse> {
        return flowOf(WeatherResponse(lat, lon))
    }

    override suspend fun addPlaceToFavorites(place: FavoritePlaceDTO) {
        favoritePlaces.add(place)
        refreshFavorites()
    }

    override fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>> {
        return observableFavoritePlaces
    }

    override suspend fun deleteFromFavorites(place: FavoritePlaceDTO) {
        favoritePlaces.remove(place)
        refreshFavorites()
    }

    override fun getMainResponse(): Flow<WeatherResponse?> {
        return flowOf(WeatherResponse(0.0, 0.0))
    }

    override fun getAlarmAlerts(): Flow<List<AlarmItem>> {
        return observableAlarmAlerts
    }

    override suspend fun deleteFromAlerts(alarm: AlarmItem) {
        alarmAlerts.remove(alarm)
        refreshAlerts()
    }

    override suspend fun insertAlarmAlert(alarm: AlarmItem) {
        alarmAlerts.add(alarm)
        refreshAlerts()
    }

    override suspend fun insertMainResponse(response: WeatherResponse) {
        weatherResponse = response
    }

    override suspend fun deleteOldResponse() {
        weatherResponse = null
    }

    private fun refreshFavorites() {
        observableFavoritePlaces.value = favoritePlaces.toList()
    }

    private fun refreshAlerts() {
        observableAlarmAlerts.value = alarmAlerts.toList()
    }
}