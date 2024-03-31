package com.example.weatherapp.ui.settings.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherapp.R
import com.example.weatherapp.sharedpref.SharedPrefManager
import com.example.weatherapp.ui.map.view.Mode
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<ListPreference>("language")
            ?.setOnPreferenceChangeListener { _, newValue ->
                val appLocale: LocaleListCompat =
                    LocaleListCompat.forLanguageTags(newValue.toString())
                AppCompatDelegate.setApplicationLocales(appLocale)
                true
            }

        findPreference<ListPreference>("location")
            ?.setOnPreferenceChangeListener { preference, newValue ->
                if (newValue.equals("gps")) {
                    if (checkPermissions() && isLocationEnabled()) {
                        getFreshLocation()
                    } else {
                        preference.setDefaultValue("map")
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle(getString(R.string.location_isn_t_enabled))
                            .setMessage(getString(R.string.please_enable_location_to_select_one_from_the_map))
                            .setNeutralButton(activity?.getString(R.string.cancel)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                } else {
                    val action =
                        SettingsFragmentDirections.actionSettingsFragmentToMapFragment(Mode.CHANGE_LOCATION)
                    findNavController().navigate(action)
                }
                true
            }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getFreshLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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
                            SharedPrefManager.getInstance(requireActivity())
                                .setCoordinates(latitude, longitude)
                        }
                        fusedClient.removeLocationUpdates(this)
                    }
                },
                Looper.getMainLooper()
            )
        }

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}