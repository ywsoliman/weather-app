package com.example.weatherapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.models.GeocodingResponse.GeocodingResponseItem

@Database(entities = [GeocodingResponseItem::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {

    abstract fun getDao(): WeatherDAO

    companion object {

        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context,
                        WeatherDatabase::class.java,
                        "weather_db"
                    )
                        .build()
                INSTANCE = instance
                instance
            }
        }

    }

}