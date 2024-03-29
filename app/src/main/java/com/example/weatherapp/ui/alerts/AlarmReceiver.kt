package com.example.weatherapp.ui.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.network.ConnectivityRepository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.example.weatherapp.repository.Repository
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val CHANNEL_ID = "1234"
private const val TAG = "AlarmReceiver"

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val currentAlarmItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent?.getSerializableExtra(
                Constants.ALARM_ITEM,
                AlarmItem::class.java
            ) else intent?.getSerializableExtra(Constants.ALARM_ITEM) as AlarmItem

        Log.i(TAG, "onReceive: currentAlarmItem = $currentAlarmItem")

        context?.let { myContext ->

            val repo = Repository.getInstance(
                WeatherLocalDataSource(WeatherDatabase.getInstance(myContext).getDao()),
                WeatherRemoteDataSource
            )

            val sharedPrefManager = SharedPrefManager.getInstance(myContext)
            val notificationManager =
                myContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationsEnabled = notificationManager.areNotificationsEnabled()
            val connectivityRepository = ConnectivityRepository(myContext)

            CoroutineScope(Dispatchers.IO).launch {

                currentAlarmItem?.let {

                    repo.deleteFromAlerts(it)

                    connectivityRepository.isConnected.collectLatest { isConnected ->
                        if (isConnected && notificationsEnabled) {
                            repo.getWeather(
                                it.latitude,
                                it.longitude,
                                exclude = "minutely,hourly,daily,alerts",
                                lang = sharedPrefManager.getLanguage(),
                            ).collectLatest {

                                val customSoundUri =
                                    Uri.parse("android.resource://" + myContext.packageName + "/raw/notification_sound")

                                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                                    .setSmallIcon(R.drawable.cloudy)
                                    .setContentTitle("Weather Alert")
                                    .setContentText(it.current?.weather?.get(0)?.description)
                                    .setAutoCancel(true)
                                    .setSound(customSoundUri)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                                val channel = NotificationChannel(
                                    CHANNEL_ID,
                                    "Weather Alerts",
                                    NotificationManager.IMPORTANCE_HIGH
                                )
                                channel.description =
                                    "This channel is specialized in receiving weather alerts."
                                notificationManager.createNotificationChannel(channel)
                                notificationManager.notify(1, builder.build())

                            }
                        }
                    }
                }
            }
        }

    }
}