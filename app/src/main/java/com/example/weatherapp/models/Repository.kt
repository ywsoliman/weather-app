package com.example.weatherapp.models

import com.example.weatherapp.db.IWeatherLocalDataSource
import com.example.weatherapp.network.IWeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow

class Repository private constructor(
    private val localDataSource: IWeatherLocalDataSource,
    private val remoteDataSource: IWeatherRemoteDataSource
) : IRepository {

    companion object {

        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(
            localDataSource: IWeatherLocalDataSource,
            remoteDataSource: IWeatherRemoteDataSource
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(localDataSource, remoteDataSource)
                INSTANCE = instance
                instance
            }
        }

    }

    override suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String,
        exclude: String,
        units: String?,
        lang: String?
    ): Flow<WeatherResponse> {
        return remoteDataSource.getWeather(lat, lon, apiKey, exclude, units, lang)
    }

    override suspend fun addPlaceToFavorites(place: FavoritePlaceDTO) {
        localDataSource.addPlaceToFavorite(place)
    }

    override suspend fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>> {
        return localDataSource.getFavoritePlaces()
    }

    override suspend fun deleteFromFavorites(place: FavoritePlaceDTO) {
        localDataSource.deleteFromFavorites(place)
    }

    override suspend fun getMainResponse(): WeatherResponse? {
        return localDataSource.getMainResponse()
    }

    override suspend fun getAlarmAlerts(): Flow<List<AlarmItem>> {
        return localDataSource.getAlarmAlerts()
    }

    override suspend fun deleteFromAlerts(alarm: AlarmItem) {
        localDataSource.deleteFromAlerts(alarm)
    }

    override suspend fun insertAlarmAlert(alarm: AlarmItem) {
        localDataSource.insertAlarmAlert(alarm)
    }


}