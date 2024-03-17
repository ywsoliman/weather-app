package com.example.weatherapp.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.IRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "MapViewModel"

class MapViewModel(private val repo: IRepository) : ViewModel() {

    fun addPlaceToFavorites(latLng: LatLng) {
        viewModelScope.launch {
            repo.getGeocoding(latLng.latitude, latLng.longitude)
                .collectLatest {
                    repo.addPlaceToFavorites(it[0])
                }
        }
    }
}