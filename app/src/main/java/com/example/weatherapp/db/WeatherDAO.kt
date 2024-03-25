package com.example.weatherapp.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {

    @Query("SELECT * FROM favorite_places")
    fun getFavoritePlaces(): Flow<List<FavoritePlaceDTO>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favoritePlace: FavoritePlaceDTO)

    @Delete
    suspend fun delete(favoritePlace: FavoritePlaceDTO)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMainResponse(response: WeatherResponse)

    @Query("SELECT * FROM main_response")
    fun getMainResponse(): Flow<WeatherResponse?>

    @Query("DELETE FROM main_response")
    suspend fun deleteMainResponse()

    @Query("SELECT * FROM alarm_alerts")
    fun getAlarmAlerts(): Flow<List<AlarmItem>>

    @Delete
    suspend fun deleteAlarmAlert(alarm: AlarmItem)

    @Insert
    suspend fun insertAlarmAlert(alarm: AlarmItem)

}