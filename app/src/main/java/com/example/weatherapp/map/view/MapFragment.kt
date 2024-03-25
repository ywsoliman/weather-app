package com.example.weatherapp.map.view

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentMapBinding
import com.example.weatherapp.models.FavoritePlaceDTO
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.SharedPrefManager
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
import java.util.Locale

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapBinding
    private lateinit var googleMap: GoogleMap
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
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

                if (mode == Mode.CHANGE_LOCATION) {
                    SharedPrefManager.getInstance(requireContext())
                        .setCoordinates(it.latitude, it.longitude)
                    findNavController().navigate(R.id.action_mapFragment_to_settingsFragment)

                } else if (mode == Mode.ADD_LOCATION) {

                    val lang = SharedPrefManager.getInstance(requireContext()).getLanguage()
                    val geocoder =
                        Geocoder(requireContext(), Locale(lang)).getFromLocation(
                            it.latitude,
                            it.longitude,
                            1
                        )?.get(0)

                    geocoder?.let { address ->
                        val favoritePlace = FavoritePlaceDTO(
                            longitude = address.longitude,
                            latitude = address.latitude,
                            countryName = address.countryName,
                            adminArea = address.adminArea,
                            subAdminArea = address.subAdminArea
                        )
                        val action =
                            MapFragmentDirections.actionMapFragmentToFavoritesFragment(favoritePlace)
                        view.findNavController().navigate(action)
                    }
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