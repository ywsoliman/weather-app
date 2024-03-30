package com.example.weatherapp.ui.home.view

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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.weatherapp.R
import com.example.weatherapp.connectivitymanager.ConnectivityRepository
import com.example.weatherapp.databinding.FragmentHomeBinding
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.example.weatherapp.repository.Repository
import com.example.weatherapp.sharedpref.SharedPrefManager
import com.example.weatherapp.ui.WeatherAnimationViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModel
import com.example.weatherapp.ui.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.util.ApiStatus
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
    private lateinit var todayForecastAdapter: HourlyWeatherAdapter
    private lateinit var nextDaysForecastAdapter: NextDaysWeatherAdapter
    private val weatherAnimationViewModel: WeatherAnimationViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(
            SharedPrefManager.getInstance(requireContext()),
            ConnectivityRepository(requireContext()),
            Repository.getInstance(
                WeatherLocalDataSource(WeatherDatabase.getInstance(requireContext()).getDao()),
                WeatherRemoteDataSource
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: HomeFragmentArgs by navArgs()
        val selectedFavoritePlace = args.favoritePlace

        if (selectedFavoritePlace != null) {
            homeViewModel.setLocationCoordinates(
                selectedFavoritePlace.latitude,
                selectedFavoritePlace.longitude,
                false,
            )
        } else {
            if (SharedPrefManager.getInstance(requireContext()).getLocation() == "gps") {
                handleGPSLocation()
            } else {
                val coord = SharedPrefManager.getInstance(requireContext()).getCoordinates()
                if (coord != null) {
                    homeViewModel.setLocationCoordinates(
                        coord.latitude,
                        coord.longitude,
                        true,
                    )
                }
            }
        }

        binding.allowBtn.setOnClickListener { askForLocation() }
        todayForecastAdapter = HourlyWeatherAdapter()
        nextDaysForecastAdapter = NextDaysWeatherAdapter()
        binding.viewModel = homeViewModel
        binding.todayAdapter = todayForecastAdapter
        binding.nextDaysAdapter = nextDaysForecastAdapter
        binding.lifecycleOwner = this

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.isConnected.collectLatest { isOnline ->
                    binding.noInternetBanner.visibility = if (isOnline) View.GONE else View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.apiStatus.collectLatest { apiStatus ->
                    when (apiStatus) {
                        is ApiStatus.Success -> {
                            binding.allowLocationCard.visibility = View.GONE
                            binding.progressBar.visibility = View.GONE
                            binding.weatherDetails.visibility = View.VISIBLE
                            todayForecastAdapter.submitList(
                                apiStatus.response.hourly.subList(0, 24)
                            )
                            nextDaysForecastAdapter.submitList(
                                apiStatus.response.daily.subList(1, 7)
                            )
                            Log.i(TAG, "handleLocationGranted: $apiStatus")
                        }

                        is ApiStatus.Failure -> {
                            Snackbar.make(
                                requireView(),
                                apiStatus.throwable.message.toString(),
                                Snackbar.LENGTH_LONG
                            )
                                .setAnchorView(R.id.bottomNavigationView)
                                .show()
                            Log.i(TAG, "handleLocationGranted: $apiStatus")
                        }

                        is ApiStatus.Loading -> {
                            binding.weatherDetails.visibility = View.GONE
                            binding.progressBar.visibility = View.VISIBLE
                            Log.i(TAG, "handleLocationGranted: $apiStatus")
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.weatherState.collectLatest {
                    binding.weatherAnimation.setWeatherData(it)
                    weatherAnimationViewModel.setWeatherState(it)
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.getCurrentTime().collectLatest {
                    binding.currentDateAndTime.text = it
                }
            }
        }
    }

    private fun handleGPSLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled())
                getFreshLocation()
            else
                enableLocationService()
        } else
            askForLocation()
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
                        Log.i(TAG, "onLocationResult: $latitude, $longitude")
                        SharedPrefManager.getInstance(requireContext())
                            .setCoordinates(latitude, longitude)
                        homeViewModel.setLocationCoordinates(latitude, longitude, true)
                    }
                    fusedClient.removeLocationUpdates(this)
                }
            },
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SharedPrefManager.getInstance(requireContext()).setLocationSettings("gps")
                getFreshLocation()
            } else {
                binding.weatherDetails.visibility = View.GONE
                binding.allowLocationCard.visibility = View.VISIBLE
            }
        }
    }

}