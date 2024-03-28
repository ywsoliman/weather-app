package com.example.weatherapp.ui.alerts

import com.example.weatherapp.models.AlarmItem

interface AlarmScheduler {
    fun schedule(item: AlarmItem)
    fun cancel(item: AlarmItem)
}