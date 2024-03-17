package com.example.weatherapp.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentFavoritesBinding
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModel
import com.example.weatherapp.models.GeocodingResponseItem
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private val favoriteViewModel: FavoriteViewModel by viewModels {
        FavoriteViewModelFactory(
            Repository.getInstance(
                WeatherLocalDataSource(requireContext()),
                WeatherRemoteDataSource
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_favorites, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.adapter = FavoritesAdapter {
            handleDeletePlaceButton(it)
        }

        binding.favoriteFAB.setOnClickListener {
            it.findNavController().navigate(R.id.action_favoritesFragment_to_mapFragment)
        }

        lifecycleScope.launch {
            favoriteViewModel.favoritePlaces.collectLatest {
                binding.adapter?.submitList(it)
            }
        }
    }

    private fun handleDeletePlaceButton(it: GeocodingResponseItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_selected_place))
            .setMessage(getString(R.string.selected_place_will_be_permanently_removed_from_favorites))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                favoriteViewModel.deleteFromFavorites(it)
            }
            .show()
    }

}