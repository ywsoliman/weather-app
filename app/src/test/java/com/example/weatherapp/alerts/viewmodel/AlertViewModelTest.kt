package com.example.weatherapp.alerts.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.MainDispatcherRule
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.models.FakeRepository
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

private const val LATITUDE = 11.1
private const val LONGITUDE = 22.2

class AlertViewModelTest {

    private lateinit var viewModel: AlertViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = AlertViewModel(FakeRepository())
    }

    @Test
    fun insertAlarmAlert() = runTest {
        val currentAlarmItem = AlarmItem(LocalDateTime.now(), LATITUDE, LONGITUDE)
        val maxAlarmItem = AlarmItem(LocalDateTime.MAX, LATITUDE, LONGITUDE)

        viewModel.insertAlarmAlert(currentAlarmItem)
        viewModel.insertAlarmAlert(maxAlarmItem)
        viewModel.getAlarmAlerts()

        val allAlarmsList = viewModel.alerts.first()
        assertThat(allAlarmsList).contains(currentAlarmItem)
        assertThat(allAlarmsList).contains(maxAlarmItem)
    }

    @Test
    fun deleteAlarmAlert() = runTest {
        val currentAlarmItem = AlarmItem(LocalDateTime.now(), LATITUDE, LONGITUDE)

        viewModel.insertAlarmAlert(currentAlarmItem)
        viewModel.deleteAlarmAlert(currentAlarmItem)
        viewModel.getAlarmAlerts()

        val allAlarmsList = viewModel.alerts.first()
        assertThat(allAlarmsList).doesNotContain(currentAlarmItem)
    }

}