package com.example.weatherapp.favorite.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.models.FakeRepository
import com.example.weatherapp.models.FavoritePlaceDTO
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FavoriteViewModelTest {

    private lateinit var viewModel: FavoriteViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        viewModel = FavoriteViewModel(FakeRepository())
    }

    @Test
    fun getFavoritePlaces() = runTest {

        val firstFavoritePlace = FavoritePlaceDTO(
            22.2,
            33.3,
            "Egypt",
            "Giza",
            "Faisal"
        )

        val secondFavoritePlace = FavoritePlaceDTO(
            11.1,
            22.2,
            "Egypt",
            "Cairo",
            "Madinet Nasr"
        )
        viewModel.addPlaceToFavorites(firstFavoritePlace)
        viewModel.addPlaceToFavorites(secondFavoritePlace)

        viewModel.getFavoritePlaces()
        val allFavoritePlaces = viewModel.favoritePlaces.first()

        assertThat(allFavoritePlaces).contains(firstFavoritePlace)
        assertThat(allFavoritePlaces).contains(secondFavoritePlace)

    }

    @Test
    fun deleteFromFavorites() = runTest {

        val favoritePlace = FavoritePlaceDTO(
            22.2,
            33.3,
            "Egypt",
            "Giza",
            "Faisal"
        )

        viewModel.addPlaceToFavorites(favoritePlace)
        viewModel.deleteFromFavorites(favoritePlace)

        viewModel.getFavoritePlaces()
        val allFavoritePlaces = viewModel.favoritePlaces.first()

        assertThat(allFavoritePlaces).doesNotContain(favoritePlace)

    }

}