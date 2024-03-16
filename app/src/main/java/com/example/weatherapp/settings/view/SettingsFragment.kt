package com.example.weatherapp.settings.view

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.weatherapp.R
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.home.viewmodel.HomeViewModel
import com.example.weatherapp.home.viewmodel.HomeViewModelFactory
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.google.android.material.chip.Chip


class SettingsFragment : Fragment() {

    private lateinit var standardChip: Chip
    private lateinit var imperialChip: Chip
    private lateinit var metricChip: Chip
    private val sharedViewModel: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            Repository.getInstance(
                WeatherLocalDataSource.getInstance(requireContext()),
                WeatherRemoteDataSource
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI(view)

        standardChip.setOnClickListener { sharedViewModel.setUnits("standard") }
        imperialChip.setOnClickListener { sharedViewModel.setUnits("imperial") }
        metricChip.setOnClickListener { sharedViewModel.setUnits("metric") }

    }

    private fun initUI(view: View) {
        standardChip = view.findViewById(R.id.standardChip)
        imperialChip = view.findViewById(R.id.imperialChip)
        metricChip = view.findViewById(R.id.metricChip)
    }

}