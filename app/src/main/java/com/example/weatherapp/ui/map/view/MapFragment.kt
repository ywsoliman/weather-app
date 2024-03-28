package com.example.weatherapp.ui.map.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.ui.map.viewmodel.MapViewModel
import com.example.weatherapp.ui.map.viewmodel.NavigationEvent
import com.example.weatherapp.util.Constants
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
    private val mapViewModel: MapViewModel by viewModels()
    private var coordinates: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: MapFragmentArgs by navArgs()
        val mode = args.mode

        Places.initialize(requireActivity().applicationContext, Constants.GOOGLE_MAPS_KEY)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.googleMapsFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        autoCompleteFragment = childFragmentManager.findFragmentById(R.id.autoCompleteFragment)
                as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(
            listOf(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG
            )
        )
        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Toast.makeText(
                    requireContext(),
                    "Error occurred in searching places.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

            override fun onPlaceSelected(place: Place) {
                val name = place.name
                val address = place.address
                val latLng = place.latLng
                latLng?.let {
                    coordinates = latLng
                    zoomOnMap(it)
                    addMaker(it)
                    binding.savePlaceLayout.visibility = View.VISIBLE
                    binding.placeNameText.visibility = View.VISIBLE
                    binding.placeNameText.text = name
                    binding.placeAddressText.text = address
                }

            }

        })

        binding.saveBtn.setOnClickListener {
            coordinates?.let {
                mapViewModel.handleSavingLocation(mode, requireContext(), it.latitude, it.longitude)
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.navigationEvent.collectLatest { event ->
                    when (event) {
                        is NavigationEvent.AddLocation -> {
                            val action =
                                MapFragmentDirections.actionMapFragmentToFavoritesFragment(event.favoritePlaceDTO)
                            findNavController().navigate(action)
                        }

                        is NavigationEvent.ChangeLocation -> {
                            findNavController().navigate(R.id.action_mapFragment_to_settingsFragment)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mapViewModel.showSnackbarEvent.collectLatest {
                    Snackbar.make(
                        requireView(),
                        "Couldn't find address for the marked point. Please choose another one.",
                        Snackbar.LENGTH_SHORT
                    )
                        .setAnchorView(R.id.savePlaceLayout)
                        .show()
                }
            }
        }


    }

    private fun addMaker(latLng: LatLng) {
        googleMap.clear()
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
        )

    }

    private fun zoomOnMap(latLng: LatLng) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.setOnMapClickListener {
            addMaker(it)
            coordinates = it
            binding.savePlaceLayout.visibility = View.VISIBLE
            binding.placeNameText.visibility = View.GONE
            val coordString = "Latitude: ${it.latitude}\nLongitude: ${it.longitude}"
            binding.placeAddressText.text = coordString
        }

    }
}