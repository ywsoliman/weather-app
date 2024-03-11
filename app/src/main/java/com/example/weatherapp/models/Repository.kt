package com.example.weatherapp.models

import com.example.weatherapp.db.IWeatherLocalDataSource
import com.example.weatherapp.network.IWeatherRemoteDataSource

class Repository private constructor(
    private val localDataSource: IWeatherLocalDataSource,
    private val remoteDataSource: IWeatherRemoteDataSource
) : IRepository {

    companion object {

        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(
            localDataSource: IWeatherLocalDataSource,
            remoteDataSource: IWeatherRemoteDataSource
        ): Repository {
            return INSTANCE ?: synchronized(this) {
                val instance = Repository(localDataSource, remoteDataSource)
                INSTANCE = instance
                instance
            }
        }

    }

}