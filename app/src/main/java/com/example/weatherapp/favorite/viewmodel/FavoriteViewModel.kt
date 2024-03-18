package com.example.weatherapp.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.GeocodingResponseItem
import com.example.weatherapp.models.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "FavoriteViewModel"

class FavoriteViewModel(private val repo: IRepository) : ViewModel() {

    private val _favoritePlaces = MutableStateFlow<List<GeocodingResponseItem>>(emptyList())
    val favoritePlaces = _favoritePlaces.asStateFlow()

    init {
        getFavoritePlaces()
    }

    private fun getFavoritePlaces() {
        viewModelScope.launch {
            repo.getFavoritePlaces()
                .collect {
                    Log.i(TAG, "getFavoritePlaces: places = $it")
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