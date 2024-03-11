package com.example.weatherapp.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
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
        binding.lifecycleOwner = this
        binding.viewModel = homeViewModel

        homeViewModel.getCurrentWeather(51.507351, -0.127758)
    }


}