package com.example.weatherapp.util

import android.content.Context
import android.location.Geocoder
import com.example.weatherapp.R
import com.example.weatherapp.sharedpref.SharedPrefManager
import java.io.IOException
import java.util.Locale

object GeocoderUtil {

    fun getAddress(context: Context, lat: Double, lon: Double): String {
        try {
            val lang = SharedPrefManager.getInstance(context).getLanguage()
            val geocoder = Geocoder(context, Locale(lang))
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val currentAddress = StringBuilder("")
                if (!address.subAdminArea.isNullOrBlank()) currentAddress.append(address.subAdminArea + ", ")
                if (!address.adminArea.isNullOrBlank()) currentAddress.append(address.adminArea + ", ")
                if (!address.countryName.isNullOrBlank()) currentAddress.append(address.countryName)
                return currentAddress.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return context.getString(R.string.unknown_location)
    }

}