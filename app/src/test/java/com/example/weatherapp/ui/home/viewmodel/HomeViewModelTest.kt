package com.example.weatherapp.ui.home.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.MainDispatcherRule
import com.example.weatherapp.models.FakeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {


    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: FakeRepository
    private lateinit var connectivityManager: FakeConnectivityManager

    @Before
    fun setup() {
        repository = FakeRepository()
        connectivityManager = FakeConnectivityManager()
        viewModel =
            HomeViewModel(FakeSharedPrefManager(), connectivityManager, repository)
    }

}
