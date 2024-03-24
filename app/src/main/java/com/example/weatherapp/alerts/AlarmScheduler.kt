package com.example.weatherapp.alerts

import com.example.weatherapp.models.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}