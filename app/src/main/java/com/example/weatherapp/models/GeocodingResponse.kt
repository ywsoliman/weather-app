package com.example.weatherapp.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GeocodingResponse(
    val response: List<GeocodingResponseItem>
)

@Entity(tableName = "favorite_places")
data class GeocodingResponseItem(
    @PrimaryKey
    val name: String,
    @SerializedName("local_names")
    @Embedded
    val localNames: LocalNames?,
    val lat: Double,
    val lon: Double,
    val country: String?,
) : Serializable {
    data class LocalNames(
        val ar: String?,
        val en: String?,
    )
}