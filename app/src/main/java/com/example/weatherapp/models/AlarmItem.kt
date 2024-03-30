package com.example.weatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity(tableName = "alarm_alerts")
data class AlarmItem(
    @PrimaryKey
    val time: LocalDateTime,
    val latitude: Double,
    val longitude: Double
) : Serializable
