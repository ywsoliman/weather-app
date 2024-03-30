package com.example.weatherapp.db

import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>>
    fun getMainResponse(): Flow<WeatherResponse?>
    fun getAlarmAlerts(): Flow<List<AlarmItem>>
    suspend fun addPlaceToFavorite(place: FavoritePlaceDTO)
    suspend fun deleteFromFavorites(place: FavoritePlaceDTO)
    suspend fun deleteFromAlerts(alarm: AlarmItem)
    suspend fun insertAlarmAlert(alarm: AlarmItem)
    suspend fun insertMainResponse(response: WeatherResponse)
    suspend fun deleteOldResponse()
}