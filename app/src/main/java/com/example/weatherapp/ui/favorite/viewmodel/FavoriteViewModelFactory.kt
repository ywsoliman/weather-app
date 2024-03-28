package com.example.weatherapp.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.models.IRepository

class FavoriteViewModelFactory(private val repo: IRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java))
            FavoriteViewModel(repo) as T
        else
            throw IllegalArgumentException("Unknown ViewModel")
    }

}