package com.example.weatherapp.ui.map.viewmodel

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.ui.map.view.Mode
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.util.SharedPrefManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Locale

sealed class NavigationEvent {
    data object ChangeLocation : NavigationEvent()
    data class AddLocation(
        val favoritePlaceDTO: FavoritePlaceDTO
    ) : NavigationEvent()
}

class MapViewModel : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _showSnackbarEvent = MutableSharedFlow<String>()
    val showSnackbarEvent = _showSnackbarEvent.asSharedFlow()

    fun handleSavingLocation(mode: Mode, context: Context, latitude: Double, longitude: Double) {

        viewModelScope.launch {
            when (mode) {
                Mode.CHANGE_LOCATION -> {
                    SharedPrefManager.getInstance(context).setCoordinates(latitude, longitude)
                    _navigationEvent.emit(NavigationEvent.ChangeLocation)
                }

                Mode.ADD_LOCATION -> {

                    val lang = SharedPrefManager.getInstance(context).getLanguage()
                    val geocoder =
                        Geocoder(context, Locale(lang)).getFromLocation(
                            latitude,
                            longitude,
                            1
                        )

                    if (!geocoder.isNullOrEmpty()) {
                        val address = geocoder[0]
                        val favoritePlace = FavoritePlaceDTO(
                            longitude = address.longitude,
                            latitude = address.latitude,
                            countryName = address.countryName,
                            adminArea = address.adminArea,
                            subAdminArea = address.subAdminArea
                        )
                        _navigationEvent.emit(
                            NavigationEvent.AddLocation(favoritePlace)
                        )
                    } else {
                        _showSnackbarEvent.emit("Couldn't find address for the marked point. Please choose another one.")
                    }
                }
            }
        }
    }
}