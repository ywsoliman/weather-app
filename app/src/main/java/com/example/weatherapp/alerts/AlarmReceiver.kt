package com.example.weatherapp.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val CHANNEL_ID = "1234"

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var repo: Repository
    private lateinit var sharedPrefManager: SharedPrefManager

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context?, intent: Intent?) {

        val currentAlarmItem =
            intent?.getSerializableExtra(Constants.ALARM_ITEM, AlarmItem::class.java)

        context?.let {

            repo = Repository.getInstance(
                WeatherLocalDataSource(it),
                WeatherRemoteDataSource
            )

            sharedPrefManager = SharedPrefManager.getInstance(it)

            CoroutineScope(Dispatchers.IO).launch {

                val currentResponse = repo.getWeather(
                    sharedPrefManager.getCoordinates()?.latitude!!,
                    sharedPrefManager.getCoordinates()?.longitude!!,
                    exclude = "minutely,hourly,daily,alerts",
                    lang = sharedPrefManager.getLanguage(),
                    units = sharedPrefManager.convertTemperatureToUnits()
                )

                currentResponse.collectLatest { response ->

                    val customSoundUri =
                        Uri.parse("android.resource://" + it.packageName + "/raw/notification_sound")

                    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.cloudy)
                        .setContentTitle("Weather Alert")
                        .setContentText(response.current?.weather?.get(0)?.description)
                        .setAutoCancel(true)
                        .setSound(customSoundUri)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)

                    val notificationManager =
                        it.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val channel = NotificationChannel(
                        CHANNEL_ID,
                        "Weather Alerts",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    channel.description = "This channel is specialized in receiving weather alerts."
                    notificationManager.createNotificationChannel(channel)
                    notificationManager.notify(1, builder.build())

                    currentAlarmItem?.let {
                        repo.deleteFromAlerts(it)
                    }

                }

            }
        }
    }

}