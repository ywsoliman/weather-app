package com.example.weatherapp.cachemanager

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.network.WeatherCache

private const val TAG = "CacheWorker"

class CacheWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        val dao = WeatherDatabase.getInstance(context).getDao()
        val cachedResponse = WeatherCache.getMainResponse()
        cachedResponse?.let {
            dao.deleteMainResponse()
            dao.insertMainResponse(it)
        }
        return Result.success()

    }

}