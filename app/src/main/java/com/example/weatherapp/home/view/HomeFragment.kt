package com.example.weatherapp.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

private const val TAG = "HomeFragment"
private const val REQUEST_LOCATION_CODE = 2005

class HomeFragment : Fragment() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var weatherTimeAdapter: WeatherTimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (checkPermissions()) {
            if (isLocationEnabled())
                getFreshLocation()
            else
                enableLocationService()
        } else
            askForLocation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherLocalDataSource(requireContext()),
                WeatherRemoteDataSource
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        weatherTimeAdapter = WeatherTimeAdapter()
        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel
        binding.adapter = weatherTimeAdapter

        homeViewModel.currentLocation.observe(viewLifecycleOwner) {
            homeViewModel.getCurrentWeather(it.first, it.second, units = "metric")
            homeViewModel.getForecastWeather(it.first, it.second, units = "metric")
        }

        homeViewModel.currentForecast.observe(viewLifecycleOwner) {
            weatherTimeAdapter.submitList(it.subList(0, 8))
        }
        
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @SuppressLint("MissingPermission")
    private fun getFreshLocation() {
        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(1000).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    val longitude = locationResult.lastLocation?.longitude
                    val latitude = locationResult.lastLocation?.latitude
                    if (latitude != null && longitude != null) {
                        homeViewModel.setLocationCoordinates(latitude, longitude)
                    }
                    Log.i(TAG, "onLocationResult: ")
                }
            },
            Looper.myLooper()
        )
    }

    private fun enableLocationService() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun askForLocation() {
        requestPermissions(
            arrayOf(
                ACCESS_COARSE_LOCATION,
            ),
            REQUEST_LOCATION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            Log.i(TAG, "onRequestPermissionsResult: grantResults = $grantResults")
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getFreshLocation()
        }
    }


}