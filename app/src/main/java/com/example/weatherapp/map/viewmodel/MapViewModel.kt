package com.example.weatherapp.map.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.models.IRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

private const val TAG = "MapViewModel"

class MapViewModel(private val repo: IRepository) : ViewModel() {

    fun addPlaceToFavorites(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getGeocoding(latLng.latitude, latLng.longitude)
                .catch {
                    Log.i(TAG, "addPlaceToFavorites: catch = ${it.message}")
                }
                .collect {
                    if (it.response.isNotEmpty())
                        repo.addPlaceToFavorites(it.response[0])
                }
        }
    }

}