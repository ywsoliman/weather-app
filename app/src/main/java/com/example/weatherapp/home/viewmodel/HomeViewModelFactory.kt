package com.example.weatherapp.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.ConnectivityRepository
import com.example.weatherapp.util.SharedPrefManager

class HomeViewModelFactory(
    private val sharedPrefManager: SharedPrefManager,
    private val connectivityRepository: ConnectivityRepository,
    private val repo: Repository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            HomeViewModel(sharedPrefManager, connectivityRepository, repo) as T
        else
            throw IllegalArgumentException("Unknown ViewModel")
    }

}