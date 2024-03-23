package com.example.weatherapp.models

import androidx.room.Embedded
import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "main_response", primaryKeys = ["lat", "lon"])
data class WeatherResponse(

    @SerializedName("lat") var lat: Double,
    @SerializedName("lon") var lon: Double,
    @SerializedName("timezone") var timezone: String? = null,
    @SerializedName("timezone_offset") var timezoneOffset: Int? = null,
    @SerializedName("current") @Embedded var current: Current? = Current(),
    @SerializedName("hourly") var hourly: ArrayList<Hourly> = arrayListOf(),
    @SerializedName("daily") var daily: ArrayList<Daily> = arrayListOf()

)

data class Current(

    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("pressure") var pressure: Int? = null,
    @SerializedName("humidity") var humidity: Int? = null,
    @SerializedName("clouds") var clouds: Int? = null,
    @SerializedName("wind_speed") var windSpeed: Double? = null,
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf()

)

data class Hourly(

    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("temp") var temp: Double? = null,
    @SerializedName("pressure") var pressure: Int? = null,
    @SerializedName("humidity") var humidity: Int? = null,
    @SerializedName("clouds") var clouds: Int? = null,
    @SerializedName("wind_speed") var windSpeed: Double? = null,
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf(),

    )

data class Temp(
    @SerializedName("min") var min: Double? = null,
    @SerializedName("max") var max: Double? = null,
)

data class Daily(

    @SerializedName("dt") var dt: Int? = null,
    @SerializedName("summary") var summary: String? = null,
    @SerializedName("temp") var temp: Temp? = Temp(),
    @SerializedName("pressure") var pressure: Int? = null,
    @SerializedName("humidity") var humidity: Int? = null,
    @SerializedName("wind_speed") var windSpeed: Double? = null,
    @SerializedName("weather") var weather: ArrayList<Weather> = arrayListOf(),
    @SerializedName("clouds") var clouds: Int? = null,

    )

data class Weather(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("main") var main: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("icon") var icon: String? = null

)