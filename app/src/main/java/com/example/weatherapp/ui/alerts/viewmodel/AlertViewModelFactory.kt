package com.example.weatherapp.ui.alerts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.repository.IRepository

class AlertViewModelFactory(private val repo: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java))
            AlertViewModel(repo) as T
        else
            throw IllegalArgumentException("Unknown ViewModel")
    }
}