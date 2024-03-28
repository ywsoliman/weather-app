package com.example.weatherapp.repository

import com.example.weatherapp.db.IWeatherLocalDataSource
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(
    private val favorites: MutableList<FavoritePlaceDTO> = mutableListOf(),
    private val alerts: MutableList<AlarmItem> = mutableListOf(),
    private val weatherResponse: WeatherResponse
) :
    IWeatherLocalDataSource {

    override suspend fun addPlaceToFavorite(place: FavoritePlaceDTO) {
        favorites.add(place)
    }

    override suspend fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>> {
        return flowOf(favorites)
    }

    override suspend fun deleteFromFavorites(place: FavoritePlaceDTO) {
        favorites.remove(place)
    }

    override suspend fun getMainResponse(): Flow<WeatherResponse?> {
        return flowOf(weatherResponse)
    }

    override suspend fun getAlarmAlerts(): Flow<List<AlarmItem>> {
        return flowOf(alerts)
    }

    override suspend fun deleteFromAlerts(alarm: AlarmItem) {
        alerts.remove(alarm)
    }

    override suspend fun insertAlarmAlert(alarm: AlarmItem) {
        alerts.add(alarm)
    }
}