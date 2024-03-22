package com.example.weatherapp.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>
) {
    data class Current(
        val dt: Long,
        val temp: Double,
        val pressure: Int,
        val humidity: Int,
        val clouds: Int,
        @SerializedName("wind_speed")
        val windSpeed: Double,
        val weather: List<Weather>
    ) {
        data class Weather(
            val id: Int,
            val main: String,
            val description: String,
            val icon: String
        )
    }

    data class Hourly(
        val dt: Long,
        val temp: Double,
        val pressure: Int,
        val humidity: Int,
        val clouds: Int,
        @SerializedName("wind_speed")
        val windSpeed: Double,
        val weather: List<Weather>,
    ) {
        data class Weather(
            val id: Int,
            val main: String,
            val description: String,
            val icon: String
        )
    }

    data class Daily(
        val dt: Long,
        val temp: Temp,
        val weather: List<Weather>,
    ) {
        data class Temp(
            val min: Double,
            val max: Double,
        )

        data class Weather(
            val id: Int,
            val main: String,
            val description: String,
            val icon: String
        )
    }
}