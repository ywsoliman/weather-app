package com.example.weatherapp.models

import androidx.room.Entity
import java.io.Serializable
import java.time.LocalDateTime

@Entity(tableName = "alarm_alerts", primaryKeys = ["time", "latitude", "longitude"])
data class AlarmItem(
    val time: LocalDateTime,
    val latitude: Double,
    val longitude: Double
) : Serializable
