package com.example.weatherapp.util

import android.location.Geocoder
import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

@BindingAdapter("url")
fun loadImage(imageView: ImageView, url: String?) {

    url?.let {

        @DrawableRes val weatherImage = when (it) {
            "01d" -> R.drawable.clear_day
            "01n" -> R.drawable.clear_night
            "03d", "03n" -> R.drawable.cloudy
            "04d", "04n" -> R.drawable.overcast
            "09d", "09n", "10d", "10n" -> R.drawable.rain
            "11d", "11n" -> R.drawable.thunderstorms
            "13d", "13n" -> R.drawable.snow
            "50d", "50n" -> R.drawable.mist
            else -> R.drawable.cloudy
        }

        imageView.setImageResource(weatherImage)

    }
}

@BindingAdapter("getTime")
fun convertEpochToTime(textView: TextView, time: Long) {
    val date = Date(time * 1000)
    val formatter = SimpleDateFormat("h a", Locale.ENGLISH)
    val formattedTime = formatter.format(date)
    textView.text = formattedTime
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("getDay")
fun convertDateToDay(textView: TextView, time: Long) {
    val instant = Instant.ofEpochSecond(time)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val dayOfWeek = localDate.dayOfWeek
    textView.text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
}

@BindingAdapter(value = ["locationLat", "locationLon"], requireAll = true)
fun getLocation(textView: TextView, lat: Double, lon: Double) {

    val lang = SharedPrefManager.getInstance(textView.context).getLanguage()
    val geocoder =
        Geocoder(textView.context, Locale(lang)).getFromLocation(
            lat,
            lon,
            1
        )

    geocoder?.let {
        if (it.isNotEmpty()) {
            val address = it[0]
            val currentAddress = StringBuilder("")
            if (!address.subAdminArea.isNullOrBlank()) currentAddress.append(address.subAdminArea + ", ")
            if (!address.adminArea.isNullOrBlank()) currentAddress.append(address.adminArea + ", ")
            if (!address.countryName.isNullOrBlank()) currentAddress.append(address.countryName)
            textView.text = currentAddress
        } else
            textView.text = textView.context.getString(R.string.unknown_location)
    }
}

@BindingAdapter("convertSpeed")
fun convertSpeedAccordingToTemp(textView: TextView, windSpeed: Double) {

    val sharedPref = SharedPrefManager.getInstance(textView.context)
    val temp = sharedPref.getTemperatureUnit()
    val speed = sharedPref.getWindSpeedUnit()

    val newWindSpeed = when (speed) {
        "m/s" -> {
            if (temp == "fahrenheit")
                convertMetersPerSecondToMilesPerHour(windSpeed)
            else
                windSpeed
        }

        else -> {
            if (temp == "kelvin" || temp == "celsius")
                convertMilesPerHourToMetersPerSecond(windSpeed)
            else
                windSpeed
        }
    }

    textView.text = String.format("%.2f %s", newWindSpeed, speed)

    // temp	-> Kelvin	Celsius	    Fahrenheit
    // wind -> m/s      m/s	        mpa
}

fun convertMetersPerSecondToMilesPerHour(num: Double): Double {
    return num * 2.237
}

fun convertMilesPerHourToMetersPerSecond(num: Double): Double {
    return num / 2.237
}