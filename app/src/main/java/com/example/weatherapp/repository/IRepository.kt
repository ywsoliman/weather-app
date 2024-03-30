package com.example.weatherapp.repository

import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.util.Constants
import kotlinx.coroutines.flow.Flow

interface IRepository {

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        exclude: String = "minutely",
        units: String? = "standard",
        lang: String? = "en"
    ): Flow<WeatherResponse>

    fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>>
    fun getMainResponse(): Flow<WeatherResponse?>
    fun getAlarmAlerts(): Flow<List<AlarmItem>>
    suspend fun addPlaceToFavorites(place: FavoritePlaceDTO)
    suspend fun deleteFromFavorites(place: FavoritePlaceDTO)
    suspend fun deleteFromAlerts(alarm: AlarmItem)
    suspend fun insertAlarmAlert(alarm: AlarmItem)
    suspend fun insertMainResponse(response: WeatherResponse)
    suspend fun deleteOldResponse()
}
