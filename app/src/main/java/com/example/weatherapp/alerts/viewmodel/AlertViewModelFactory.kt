package com.example.weatherapp.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.models.IRepository

class AlertViewModelFactory(private val repo: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java))
            AlertViewModel(repo) as T
        else
            throw IllegalArgumentException("Unknown ViewModel")
    }
}