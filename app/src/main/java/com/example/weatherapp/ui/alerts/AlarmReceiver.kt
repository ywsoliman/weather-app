package com.example.weatherapp.ui.alerts

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weatherapp.R
import com.example.weatherapp.connectivitymanager.ConnectivityRepository
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.example.weatherapp.repository.Repository
import com.example.weatherapp.sharedpref.SharedPrefManager
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.GeocoderUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private const val CHANNEL_ID = "1234"

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val currentAlarmItem = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent?.getSerializableExtra(
                Constants.ALARM_ITEM,
                AlarmItem::class.java
            ) else intent?.getSerializableExtra(Constants.ALARM_ITEM) as AlarmItem


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
                            ).collectLatest { response ->

                                val soundUri =
                                    Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + myContext.packageName + "/" + R.raw.notification_sound)

                                val channel = createChannel(context, soundUri)
                                notificationManager.createNotificationChannel(channel)

                                val builder = buildNotification(myContext, response, soundUri)

                                notificationManager.notify(1, builder.build())

                            }
                        }
                    }
                }
            }
        }

    }

    private fun buildNotification(
        myContext: Context,
        response: WeatherResponse,
        soundUri: Uri?
    ): NotificationCompat.Builder {

        val address =
            GeocoderUtil.getAddress(myContext, response.lat, response.lon)

        return NotificationCompat.Builder(myContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.cloudy)
            .setContentTitle(myContext.getString(R.string.weather_alert))
            .setContentText(
                myContext.getString(
                    R.string.current_weather_at_is,
                    address,
                    response.current?.weather?.get(0)?.description
                )
            )
            .setSound(soundUri)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    private fun createChannel(
        context: Context,
        soundUri: Uri?
    ): NotificationChannel {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.weather_alert),
            NotificationManager.IMPORTANCE_HIGH,
        )
        channel.description =
            context.getString(R.string.this_channel_is_specialized_in_receiving_weather_alerts)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        channel.setSound(soundUri, audioAttributes)
        return channel
    }
}