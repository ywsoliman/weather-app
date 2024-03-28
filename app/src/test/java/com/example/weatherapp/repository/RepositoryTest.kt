package com.example.weatherapp.repository

import com.example.weatherapp.db.IWeatherLocalDataSource
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.WeatherResponse
import com.example.weatherapp.network.IWeatherRemoteDataSource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class RepositoryTest {

    private val firstFavoritePlace = FavoritePlaceDTO(
        22.2,
        33.3,
        "Egypt",
        "Giza",
        "Faisal"
    )

    private val secondFavoritePlace = FavoritePlaceDTO(
        11.1,
        22.2,
        "Egypt",
        "Cairo",
        "Madinet Nasr"
    )
    private val currentAlarmItem = AlarmItem(LocalDateTime.now())
    private val maxAlarmItem = AlarmItem(LocalDateTime.MAX)
    private val weatherResponse = WeatherResponse(11.1, 22.2)

    private val localFavorites = listOf(firstFavoritePlace, secondFavoritePlace)
    private val localAlerts = listOf(currentAlarmItem, maxAlarmItem)

    private lateinit var localDataSource: IWeatherLocalDataSource
    private lateinit var remoteDataSource: IWeatherRemoteDataSource

    private lateinit var repository: Repository

    @Before
    fun setup() {
        localDataSource =
            FakeLocalDataSource(
                localFavorites.toMutableList(),
                localAlerts.toMutableList(),
                weatherResponse
            )
        remoteDataSource = FakeRemoteDataSource()
        repository = Repository.getInstance(localDataSource, remoteDataSource)
    }

    @Test
    fun getFavoritePlaces() = runTest {
        val favoritePlaces = repository.getFavoritePlaces().first()
        assertThat(favoritePlaces).isEqualTo(localFavorites)
    }

    @Test
    fun getAlarmAlerts() = runTest {
        val alarmAlerts = repository.getAlarmAlerts().first()
        assertThat(alarmAlerts).isEqualTo(localAlerts)
    }

    @Test
    fun insertFavoritePlace() = runTest {
        val newFavoritePlace = FavoritePlaceDTO(33.3, 44.4, "France", "Paris", "Paris")
        repository.addPlaceToFavorites(newFavoritePlace)
        val favoritePlaces = repository.getFavoritePlaces().first()
        assertThat(favoritePlaces).contains(newFavoritePlace)
    }

    @Test
    fun deleteFavoritePlace() = runTest {
        repository.deleteFromFavorites(firstFavoritePlace)
        val favoritePlaces = repository.getFavoritePlaces().first()
        assertThat(favoritePlaces).doesNotContain(firstFavoritePlace)
    }

    @Test
    fun insertAlarmAlert() = runTest {
        val newAlarmAlert = AlarmItem(LocalDateTime.MIN)
        repository.insertAlarmAlert(newAlarmAlert)
        val alarmAlerts = repository.getAlarmAlerts().first()
        assertThat(alarmAlerts).contains(newAlarmAlert)
    }

    @Test
    fun deleteAlarmAlert() = runTest {
        repository.deleteFromAlerts(currentAlarmItem)
        val alarmAlerts = repository.getAlarmAlerts().first()
        assertThat(alarmAlerts).doesNotContain(currentAlarmItem)
    }

    @Test
    fun getWeather() = runTest {
        val currentWeather = repository.getWeather(11.1, 22.2).first()
        assertThat(currentWeather).isEqualTo(weatherResponse)
    }

}