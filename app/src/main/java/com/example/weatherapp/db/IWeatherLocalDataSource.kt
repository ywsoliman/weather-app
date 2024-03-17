package com.example.weatherapp.db

import com.example.weatherapp.models.GeocodingResponseItem
import kotlinx.coroutines.flow.Flow

interface IWeatherLocalDataSource {
    suspend fun addPlaceToFavorite(place: GeocodingResponseItem)
    suspend fun getFavoritePlaces(): Flow<List<GeocodingResponseItem>>
    suspend fun deleteFromFavorites(place: GeocodingResponseItem)
}