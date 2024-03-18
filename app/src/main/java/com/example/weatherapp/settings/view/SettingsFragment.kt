package com.example.weatherapp.settings.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.example.weatherapp.R
import com.example.weatherapp.map.view.Mode
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val TAG = "SettingsFragment"

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
                        // getCurrentLocation
                    } else {
                        preference.setDefaultValue("Not set")
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Location isn't enabled")
                            .setMessage("Please enable location to select one from the map.")
                            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
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

    private fun isLocationEnabled(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}