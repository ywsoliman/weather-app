package com.example.weatherapp.db

import android.content.Context
import com.example.weatherapp.models.FavoritePlaceDTO
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(context: Context) : IWeatherLocalDataSource {

    private val dao = WeatherDatabase.getInstance(context).getDao()

    companion object {

        @Volatile
        private var INSTANCE: WeatherLocalDataSource? = null

        fun getInstance(
            context: Context
        ): WeatherLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                val instance = WeatherLocalDataSource(context)
                INSTANCE = instance
                instance
            }
        }

    }

    override suspend fun addPlaceToFavorite(place: FavoritePlaceDTO) {
        dao.insert(place)
    }

    override suspend fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>> {
        return dao.getFavoritePlaces()
    }

    override suspend fun deleteFromFavorites(place: FavoritePlaceDTO) {
        dao.delete(place)
    }

}