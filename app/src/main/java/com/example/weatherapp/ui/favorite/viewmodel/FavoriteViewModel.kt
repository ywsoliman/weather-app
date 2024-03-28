package com.example.weatherapp.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: IRepository) : ViewModel() {

    private val _favoritePlaces = MutableStateFlow<List<FavoritePlaceDTO>>(emptyList())
    val favoritePlaces = _favoritePlaces.asStateFlow()

    init {
        getFavoritePlaces()
    }

    fun getFavoritePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavoritePlaces()
                .collect {
                    _favoritePlaces.value = it
                }
        }
    }

    fun deleteFromFavorites(place: FavoritePlaceDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFromFavorites(place)
            getFavoritePlaces()
        }
    }

    fun addPlaceToFavorites(favoritePlace: FavoritePlaceDTO) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addPlaceToFavorites(favoritePlace)
        }
    }

}