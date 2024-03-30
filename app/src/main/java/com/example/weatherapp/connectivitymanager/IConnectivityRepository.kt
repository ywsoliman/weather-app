package com.example.weatherapp.connectivitymanager

import kotlinx.coroutines.flow.StateFlow

interface IConnectivityRepository {
    val isConnected: StateFlow<Boolean>
}