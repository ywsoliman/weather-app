package com.example.weatherapp.models

import com.example.weatherapp.util.Constants
import kotlinx.coroutines.flow.Flow

interface IRepository {

    suspend fun getWeather(
        lat: Double,
        lon: Double,
        apiKey: String = Constants.API_KEY,
        exclude: String = "minutely",
        units: String? = "metric",
        lang: String? = "en"
    ): Flow<WeatherResponse>

    suspend fun addPlaceToFavorites(place: FavoritePlaceDTO)
    suspend fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>>
    suspend fun deleteFromFavorites(place: FavoritePlaceDTO)
    suspend fun getMainResponse(): WeatherResponse?
}
