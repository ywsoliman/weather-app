package com.example.weatherapp.home.view

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherapp.R
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.example.weatherapp.util.ApiStatus
import com.example.weatherapp.util.LocationStatus
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "HomeFragment"
private const val REQUEST_LOCATION_CODE = 2005

class HomeFragment : Fragment() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var todayForecastAdapter: WeatherTimeAdapter
    private lateinit var nextDaysForecastAdapter: NextDaysWeatherAdapter

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.allowBtn.setOnClickListener { askForLocation() }

        val homeViewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                WeatherLocalDataSource(requireContext()),
                WeatherRemoteDataSource
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        todayForecastAdapter = WeatherTimeAdapter()
        nextDaysForecastAdapter = NextDaysWeatherAdapter()
        binding.viewModel = homeViewModel
        binding.todayAdapter = todayForecastAdapter
        binding.nextDaysAdapter = nextDaysForecastAdapter
        binding.lifecycleOwner = this

        lifecycleScope.launch {
            homeViewModel.locationStatus.collectLatest { locationStatus ->
                when (locationStatus) {
                    is LocationStatus.Granted -> {
                        handleLocationGranted(locationStatus.latitude, locationStatus.longitude)
                    }

                    is LocationStatus.Denied -> {
                        binding.weatherDetails.visibility = View.GONE
                        binding.allowLocationCard.visibility = View.VISIBLE
                    }

                    is LocationStatus.Asking -> {
                        binding.weatherDetails.visibility = View.GONE
                        binding.allowLocationCard.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launch {
            homeViewModel.todayForecast.collect {
                todayForecastAdapter.submitList(it)
            }
        }
        lifecycleScope.launch {
            homeViewModel.nextDaysForecast.collect {
                nextDaysForecastAdapter.submitList(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleLocationGranted(lat: Double, lon: Double) {
        homeViewModel.getCurrentWeather(
            lat,
            lon,
            units = "metric"
        )
        homeViewModel.getForecastWeather(
            lat,
            lon,
            units = "metric"
        )
        lifecycleScope.launch {
            homeViewModel.apiStatus.collectLatest { apiStatus ->
                when (apiStatus) {
                    is ApiStatus.Success -> {
                        binding.allowLocationCard.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        binding.weatherDetails.visibility = View.VISIBLE
                    }

                    is ApiStatus.Failure -> {
                        Snackbar.make(
                            requireView(),
                            "There is a problem with the server. Couldn't fetch the weather.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }

                    is ApiStatus.Loading -> {
                        binding.weatherDetails.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }
            }
        }
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
                        Log.i(TAG, "onLocationResult: latitude = $latitude, longitude = $longitude")
                    }
                    fusedClient.removeLocationUpdates(this)
                }
            },
            Looper.myLooper()
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
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getFreshLocation()
//                homeViewModel.getFreshLocation()
            } else
                homeViewModel.locationDenied()
        }
    }


}