package com.example.weatherapp.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "MapViewModel"

class MapViewModel(private val repo: IRepository) : ViewModel() {
    fun addPlaceToFavorites(favoritePlace: FavoritePlaceDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addPlaceToFavorites(favoritePlace)
        }
    }

}