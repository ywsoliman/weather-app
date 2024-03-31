package com.example.weatherapp.db

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
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
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: WeatherDatabase
    private lateinit var dao: WeatherDAO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.getDao()
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
        dao.insertFavorite(favoritePlace)
        val allFavoritePlaces = dao.getFavoritePlaces().first()
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
        dao.insertFavorite(favoritePlace)
        dao.delete(favoritePlace)
        val allFavoritePlaces = dao.getFavoritePlaces().first()
        assertThat(allFavoritePlaces).doesNotContain(favoritePlace)
    }

    @Test
    fun insertAlarmAlert() = runTest {
        val alarmItem = AlarmItem(LocalDateTime.now(), 11.1, 22.2)
        dao.insertAlarmAlert(alarmItem)
        val allAlarmItems = dao.getAlarmAlerts().first()
        assertThat(allAlarmItems).contains(alarmItem)
    }

    @Test
    fun deleteAlarmItem() = runTest {
        val alarmItem = AlarmItem(LocalDateTime.now(), 11.1, 22.2)
        dao.insertAlarmAlert(alarmItem)
        dao.deleteAlarmAlert(alarmItem)
        val allAlarmItems = dao.getAlarmAlerts().first()
        assertThat(allAlarmItems).doesNotContain(alarmItem)
    }

    @Test
    fun insertMainResponse() = runTest {
        val response = WeatherResponse(11.1, 22.2)
        dao.insertMainResponse(response)
        val mainResponse = dao.getMainResponse().first()
        assertThat(mainResponse).isNotNull()
        assertThat(mainResponse).isEqualTo(response)
    }

    @Test
    fun deleteMainResponse() = runTest {
        val response = WeatherResponse(11.1, 22.2)
        dao.insertMainResponse(response)
        dao.deleteMainResponse()
        val mainResponse = dao.getMainResponse().first()
        assertThat(mainResponse).isNull()
        assertThat(mainResponse).isNotEqualTo(response)
    }

}