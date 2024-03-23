package com.example.weatherapp.util

import androidx.room.TypeConverter
import com.example.weatherapp.models.Daily
import com.example.weatherapp.models.Hourly
import com.example.weatherapp.models.Weather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class Converters {

    @TypeConverter
    fun fromWeatherString(value: String?): ArrayList<Weather>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<ArrayList<Weather>>() {}.type
        return Gson().fromJson<ArrayList<Weather>>(value, listType)
    }

    @TypeConverter
    fun fromWeatherList(list: ArrayList<Weather>?): String? {
        return if (list == null) {
            null
        } else {
            Gson().toJson(list)
        }
    }

    @TypeConverter
    fun fromHourlyString(value: String?): ArrayList<Hourly>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<ArrayList<Hourly>>() {}.type
        return Gson().fromJson<ArrayList<Hourly>>(value, listType)
    }

    @TypeConverter
    fun fromHourlyList(list: ArrayList<Hourly>?): String? {
        return if (list == null) {
            null
        } else {
            Gson().toJson(list)
        }
    }

    @TypeConverter
    fun fromDailyString(value: String?): ArrayList<Daily>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<ArrayList<Daily>>() {}.type
        return Gson().fromJson<ArrayList<Daily>>(value, listType)
    }

    @TypeConverter
    fun fromDailyList(list: ArrayList<Daily>?): String? {
        return if (list == null) {
            null
        } else {
            Gson().toJson(list)
        }
    }

}