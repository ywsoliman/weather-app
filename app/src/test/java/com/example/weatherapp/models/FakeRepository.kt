package com.example.weatherapp.models

import com.example.weatherapp.repository.IRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRepository : IRepository {

    private val favoritePlaces = mutableListOf<FavoritePlaceDTO>()
    private val observableFavoritePlaces = MutableStateFlow<List<FavoritePlaceDTO>>(favoritePlaces)

    private val alarmAlerts = mutableListOf<AlarmItem>()
    private val observableAlarmAlerts = MutableStateFlow<List<AlarmItem>>(alarmAlerts)


    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        exclude: String,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun addPlaceToFavorites(place: FavoritePlaceDTO) {
        favoritePlaces.add(place)
        refreshFavorites()
    }

    override suspend fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>> {
        return observableFavoritePlaces
    }

    override suspend fun deleteFromFavorites(place: FavoritePlaceDTO) {
        favoritePlaces.remove(place)
        refreshFavorites()
    }

    override suspend fun getMainResponse(): Flow<WeatherResponse?> {
        TODO("Not yet implemented")
    }

    override suspend fun getAlarmAlerts(): Flow<List<AlarmItem>> {
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

    private fun refreshFavorites() {
        observableFavoritePlaces.value = favoritePlaces.toList()
    }
    private fun refreshAlerts() {
        observableAlarmAlerts.value = alarmAlerts.toList()
    }
}