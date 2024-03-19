package com.example.weatherapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GeocodingResponse : ArrayList<GeocodingResponse.GeocodingResponseItem>() {

    @Entity(tableName = "favorite_places")
    data class GeocodingResponseItem(
        @PrimaryKey
        val name: String,
        @Embedded
        @SerializedName("local_names")
        val localNames: LocalNames?,
        val lat: Double,
        val lon: Double,
        val country: String,
        val state: String
    ) : Serializable {
        data class LocalNames(
            val ar: String?,
            val en: String?,
        )
    }
}