package com.example.weatherapp.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.models.Repository

class HomeViewModelFactory(private val repo: Repository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java))
            HomeViewModel(repo) as T
        else
            throw IllegalArgumentException("Unknown ViewModel")
    }

}