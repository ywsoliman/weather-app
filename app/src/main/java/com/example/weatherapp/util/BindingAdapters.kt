package com.example.weatherapp.util

import android.location.Geocoder
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.example.weatherapp.R
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

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
    val locale = Locale(SharedPrefManager.getInstance(textView.context).getLanguage())
    val date = Date(time * 1000)
    val formatter = SimpleDateFormat("h a", locale)
    val formattedTime = formatter.format(date)
    textView.text = formattedTime
}

@BindingAdapter("getDay")
fun convertDateToDay(textView: TextView, time: Long) {
    val locale = Locale(SharedPrefManager.getInstance(textView.context).getLanguage())
    val instant = Instant.ofEpochSecond(time)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    val dayOfWeek = localDate.dayOfWeek
    textView.text = dayOfWeek.getDisplayName(TextStyle.SHORT, locale)
}

@BindingAdapter(value = ["locationLat", "locationLon"], requireAll = true)
fun getLocation(textView: TextView, lat: Double, lon: Double) {

    try {
        val lang = SharedPrefManager.getInstance(textView.context).getLanguage()
        val geocoder = Geocoder(textView.context, Locale(lang))
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val currentAddress = StringBuilder("")
            if (!address.subAdminArea.isNullOrBlank()) currentAddress.append(address.subAdminArea + ", ")
            if (!address.adminArea.isNullOrBlank()) currentAddress.append(address.adminArea + ", ")
            if (!address.countryName.isNullOrBlank()) currentAddress.append(address.countryName)
            textView.text = currentAddress
        } else {
            textView.text = textView.context.getString(R.string.unknown_location)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

@BindingAdapter(value = ["temp", "maxTemp"], requireAll = false)
fun convertTempUnit(textView: TextView, temp: Double, maxTemp: Double?) {

    val sharedPref = SharedPrefManager.getInstance(textView.context)
    val tempUnit = sharedPref.getTemperatureUnit()
    val minWeatherDegree =
        when (tempUnit) {
            "fahrenheit" -> convertFromKelvinToFahrenheit(temp)
            "celsius" -> convertFromKelvinToCelsius(temp)
            else -> temp.roundToInt()
        }

    val maxWeatherDegree = maxTemp?.let { max ->
        when (tempUnit) {
            "fahrenheit" -> convertFromKelvinToFahrenheit(max)
            "celsius" -> convertFromKelvinToCelsius(max)
            else -> max.roundToInt()
        }
    }

    val formattedTemp = if (maxWeatherDegree != null) {
        textView.context.getString(
            R.string.minAndMaxTemp,
            minWeatherDegree,
            maxWeatherDegree
        )
    } else {
        textView.context.getString(R.string.temperature, minWeatherDegree)
    }

    textView.text = formattedTemp
}

fun convertFromKelvinToFahrenheit(temp: Double): Int {
    return ((temp - 273.15) * 9 / 5 + 32).roundToInt()
}

fun convertFromKelvinToCelsius(temp: Double): Int {
    return (temp - 273.15).roundToInt()
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

@BindingAdapter("formatDate")
fun formatDateToHumanReadable(textView: TextView, time: LocalDateTime) {
    val locale = SharedPrefManager.getInstance(textView.context).getLanguage()
    val formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy - hh:mm a", Locale(locale))
    textView.text = formatter.format(time)
}


fun convertMetersPerSecondToMilesPerHour(num: Double): Double {
    return num * 2.237
}

fun convertMilesPerHourToMetersPerSecond(num: Double): Double {
    return num / 2.237
}