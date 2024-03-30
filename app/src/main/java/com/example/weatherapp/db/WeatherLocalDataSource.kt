package com.example.weatherapp.db

import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(private val dao: WeatherDAO) : IWeatherLocalDataSource {

    companion object {

        @Volatile
        private var INSTANCE: WeatherLocalDataSource? = null

        fun getInstance(
            dao: WeatherDAO
        ): WeatherLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherLocalDataSource(dao)
                INSTANCE = instance
                instance
            }
        }

    }

    override suspend fun addPlaceToFavorite(place: FavoritePlaceDTO) {
        dao.insertFavorite(place)
    }

    override fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>> {
        return dao.getFavoritePlaces()
    }

    override suspend fun deleteFromFavorites(place: FavoritePlaceDTO) {
        dao.delete(place)
    }

    override fun getMainResponse(): Flow<WeatherResponse?> {
        return dao.getMainResponse()
    }

    override fun getAlarmAlerts(): Flow<List<AlarmItem>> {
        return dao.getAlarmAlerts()
    }

    override suspend fun deleteFromAlerts(alarm: AlarmItem) {
        return dao.deleteAlarmAlert(alarm)
    }

    override suspend fun insertAlarmAlert(alarm: AlarmItem) {
        return dao.insertAlarmAlert(alarm)
    }

    override suspend fun insertMainResponse(response: WeatherResponse) {
        dao.insertMainResponse(response)
    }

    override suspend fun deleteOldResponse() {
        dao.deleteMainResponse()
    }

}