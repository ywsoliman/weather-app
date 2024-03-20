package com.example.weatherapp.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weatherapp.models.FavoritePlaceDTO
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class WeatherLocalDataSourceTest {

    private lateinit var database: WeatherDatabase
    private lateinit var localDataSource: WeatherLocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        localDataSource = WeatherLocalDataSource(ApplicationProvider.getApplicationContext())
    }

    @After
    fun shutdown() {
        database.close()
    }

    @Test
    fun insertFavoritePlace() = runTest {
        val favoritePlace = FavoritePlaceDTO(
            22.2,
            33.3,
            "Egypt",
            "Giza",
            "Faisal"
        )
        localDataSource.addPlaceToFavorite(favoritePlace)
        val allFavoritePlaces = localDataSource.getFavoritePlaces().first()
        assertThat(allFavoritePlaces).contains(favoritePlace)
    }

    @Test
    fun deleteFavoritePlace() = runTest {
        val favoritePlace = FavoritePlaceDTO(
            22.2,
            33.3,
            "Egypt",
            "Giza",
            "Faisal"
        )
        localDataSource.addPlaceToFavorite(favoritePlace)
        localDataSource.deleteFromFavorites(favoritePlace)
        val allFavoritePlaces = localDataSource.getFavoritePlaces().first()
        assertThat(allFavoritePlaces).doesNotContain(favoritePlace)
    }

}