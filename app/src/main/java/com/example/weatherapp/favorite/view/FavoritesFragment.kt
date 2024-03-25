package com.example.weatherapp.favorite.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentFavoritesBinding
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModel
import com.example.weatherapp.favorite.viewmodel.FavoriteViewModelFactory
import com.example.weatherapp.map.view.Mode
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.ConnectivityRepository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var connectivityRepository: ConnectivityRepository
    private lateinit var favoriteAdapter: FavoritesAdapter
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
        connectivityRepository = ConnectivityRepository(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: FavoritesFragmentArgs by navArgs()
        args.favoritePlace?.let {
            favoriteViewModel.addPlaceToFavorites(it)
        }

        favoriteAdapter = FavoritesAdapter({
            val action = FavoritesFragmentDirections.actionFavoritesFragmentToHomeFragment(it)
            findNavController().navigate(action)
        }, {
            handleDeletePlaceButton(it)
        })

        binding.adapter = favoriteAdapter

        binding.favoriteFAB.setOnClickListener {
            lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    connectivityRepository.isConnected.collectLatest { isOnline ->
                        if (isOnline) {
                            val action =
                                FavoritesFragmentDirections.actionFavoritesFragmentToMapFragment(
                                    Mode.ADD_LOCATION
                                )
                            view.findNavController().navigate(action)
                        } else {
                            Snackbar.make(
                                requireView(),
                                getString(R.string.can_t_add_to_favorite_while_internet_is_not_available),
                                Snackbar.LENGTH_SHORT
                            )
                                .setAnchorView(R.id.bottomNavigationView)
                                .show()
                        }
                    }
                }
            }

        }


        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favoriteViewModel.favoritePlaces.collect {
                    if (it.isEmpty()) {
                        favoriteAdapter.submitList(emptyList())
                        binding.noPlacesImage.visibility = View.VISIBLE
                        binding.noPlacesText.visibility = View.VISIBLE
                    } else {
                        binding.noPlacesImage.visibility = View.GONE
                        binding.noPlacesText.visibility = View.GONE
                        favoriteAdapter.submitList(it)
                    }
                }
            }
        }
    }

    private fun handleDeletePlaceButton(it: FavoritePlaceDTO) {
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