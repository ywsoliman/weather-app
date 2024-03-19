package com.example.weatherapp.db

import com.example.weatherapp.models.FavoritePlaceDTO
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun addPlaceToFavorite(place: FavoritePlaceDTO)
    suspend fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>>
    suspend fun deleteFromFavorites(place: FavoritePlaceDTO)
}