package com.example.weatherapp.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "FavoriteViewModel"

class FavoriteViewModel(private val repo: IRepository) : ViewModel() {

    private val _favoritePlaces = MutableStateFlow<List<FavoritePlaceDTO>>(emptyList())
    val favoritePlaces = _favoritePlaces.asStateFlow()

    init {
        getFavoritePlaces()
    }

    private fun getFavoritePlaces() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getFavoritePlaces()
                .collect {
                    Log.i(TAG, "getFavoritePlaces: places = $it")
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

}