package com.example.weatherapp.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertViewModel(private val repo: IRepository) : ViewModel() {

    private val _alerts = MutableStateFlow<List<AlarmItem>>(emptyList())
    val alerts = _alerts.asStateFlow()

    init {
        getAlarmAlerts()
    }

    fun getAlarmAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAlarmAlerts()
                .collectLatest {
                    _alerts.value = it
                }
        }
    }

    fun insertAlarmAlert(alarm: AlarmItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.insertAlarmAlert(alarm)
        }
    }

    fun deleteAlarmAlert(alarm: AlarmItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFromAlerts(alarm)
            getAlarmAlerts()
        }
    }

}