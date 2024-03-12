package com.example.weatherapp.util

import android.os.Build
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("url")
fun loadImage(imageView: ImageView, url: String?) {
    url?.let {
        Glide.with(imageView.context)
            .load("https://openweathermap.org/img/wn/$url@2x.png")
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_broken_image)
            .into(imageView)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@BindingAdapter("getTime")
fun formatTimeToAmPm(textView: TextView, time: String) {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTime = LocalDateTime.parse(time, formatter)
    textView.text = dateTime.format(DateTimeFormatter.ofPattern("h a"))
}

