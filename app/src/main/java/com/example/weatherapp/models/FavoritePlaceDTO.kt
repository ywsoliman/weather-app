package com.example.weatherapp.models

import androidx.room.Entity
import java.io.Serializable

@Entity(tableName = "favorite_places", primaryKeys = ["longitude", "latitude"])
data class FavoritePlaceDTO(
    val longitude: Double,
    val latitude: Double,
    val countryName: String?,
    val adminArea: String?,
    val subAdminArea: String?
) : Serializable {

    override fun toString(): String {
        val placeString = StringBuilder()
        subAdminArea?.let { placeString.append(it).append(", ") }
        adminArea?.let { placeString.append(it).append(", ") }
        countryName?.let { placeString.append(it) }
        return placeString.toString()
    }

}
