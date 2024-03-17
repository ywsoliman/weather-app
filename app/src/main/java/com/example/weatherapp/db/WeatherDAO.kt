package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.GeocodingResponseItem
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {

    @Query("SELECT * FROM favorite_places")
    fun getFavoritePlaces(): Flow<List<GeocodingResponseItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(geocodingResponseItem: GeocodingResponseItem)

    @Delete
    suspend fun delete(geocodingResponseItem: GeocodingResponseItem)

}