package com.example.weatherapp.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.GeocodingResponseItem
import com.example.weatherapp.models.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: IRepository) : ViewModel() {

    private val _favoritePlaces = MutableStateFlow<List<GeocodingResponseItem>>(emptyList())
    val favoritePlaces = _favoritePlaces.asStateFlow()

    init {
        getFavoritePlaces()
    }

    private fun getFavoritePlaces() {
        viewModelScope.launch {
            repo.getFavoritePlaces()
                .collectLatest {
                    _favoritePlaces.value = it
                }
        }
    }

    fun deleteFromFavorites(place: GeocodingResponseItem) {
        viewModelScope.launch {
            repo.deleteFromFavorites(place)
        }
    }

}