package com.example.weatherapp.util

import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.preference.PreferenceManager
import com.example.weatherapp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("getTime")
fun formatTimeToAmPm(textView: TextView, time: String) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(time, formatter)
    textView.text = dateTime.format(DateTimeFormatter.ofPattern("h a"))
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("getDay")
fun convertDateToDay(textView: TextView, time: String) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val date = LocalDate.parse(time, formatter)
    textView.text = date.dayOfWeek.toString().substring(0, 3)
}

@BindingAdapter("convertSpeed")
fun convertSpeedAccordingToTemp(textView: TextView, windSpeed: Double) {

    val settingsPref = PreferenceManager.getDefaultSharedPreferences(textView.context)
    val temp = settingsPref.getString("temperature", "celsius")
    val speed = settingsPref.getString("wind", "m/s")

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