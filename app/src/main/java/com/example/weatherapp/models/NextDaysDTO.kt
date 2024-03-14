package com.example.weatherapp.models

data class NextDaysDTO(
    val time: String,
    val icon: String,
    val description: String,
    val minTemp: Double,
    val maxTemp: Double
)