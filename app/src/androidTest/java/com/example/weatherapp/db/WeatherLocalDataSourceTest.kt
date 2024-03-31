package com.example.weatherapp.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

@MediumTest
class WeatherLocalDataSourceTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherDatabase
    private lateinit var localDataSource: WeatherLocalDataSource

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        localDataSource = WeatherLocalDataSource(database.getDao())
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

    @Test
    fun insertAlarmAlert() = runTest {
        val alarmItem = AlarmItem(LocalDateTime.now(), 11.1, 22.2)
        localDataSource.insertAlarmAlert(alarmItem)
        val allAlarmItems = localDataSource.getAlarmAlerts().first()
        assertThat(allAlarmItems).contains(alarmItem)
    }

    @Test
    fun deleteAlarmItem() = runTest {
        val alarmItem = AlarmItem(LocalDateTime.now(), 11.1, 22.2)
        localDataSource.insertAlarmAlert(alarmItem)
        localDataSource.deleteFromAlerts(alarmItem)
        val allAlarmItems = localDataSource.getAlarmAlerts().first()
        assertThat(allAlarmItems).doesNotContain(alarmItem)
    }

    @Test
    fun insertMainResponse() = runTest {
        val response = WeatherResponse(11.1, 22.2)
        localDataSource.insertMainResponse(response)
        val mainResponse = localDataSource.getMainResponse().first()
        assertThat(mainResponse).isNotNull()
        assertThat(mainResponse).isEqualTo(response)
    }

    @Test
    fun deleteMainResponse() = runTest {
        val response = WeatherResponse(11.1, 22.2)
        localDataSource.insertMainResponse(response)
        localDataSource.deleteOldResponse()
        val mainResponse = localDataSource.getMainResponse().first()
        assertThat(mainResponse).isNull()
        assertThat(mainResponse).isNotEqualTo(response)
    }

}