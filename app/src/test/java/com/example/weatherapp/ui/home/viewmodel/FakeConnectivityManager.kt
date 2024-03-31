package com.example.weatherapp.ui.home.viewmodel

import com.example.weatherapp.connectivitymanager.IConnectivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeConnectivityManager : IConnectivityRepository {
    private val _isConnected = MutableStateFlow(true)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected

    fun setIsConnected(connected: Boolean) {
        _isConnected.value = connected
    }
}