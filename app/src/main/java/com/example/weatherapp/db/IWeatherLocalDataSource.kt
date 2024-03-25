package com.example.weatherapp.db

import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun addPlaceToFavorite(place: FavoritePlaceDTO)
    suspend fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>>
    suspend fun deleteFromFavorites(place: FavoritePlaceDTO)
    suspend fun getMainResponse(): Flow<WeatherResponse?>
    suspend fun getAlarmAlerts(): Flow<List<AlarmItem>>
    suspend fun deleteFromAlerts(alarm: AlarmItem)
    suspend fun insertAlarmAlert(alarm: AlarmItem)
}