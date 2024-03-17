package com.example.weatherapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)


        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> navigateToFragment(R.id.homeFragment)
                R.id.favoritesFragment -> navigateToFragment(R.id.favoritesFragment)
                R.id.alertsFragment -> navigateToFragment(R.id.alertsFragment)
                R.id.settingsFragment -> navigateToFragment(R.id.settingsFragment)
            }
            true
        }
    }

    private fun navigateToFragment(fragmentId: Int) {
        if (navController.currentDestination?.id != fragmentId) {
            navController.popBackStack(R.id.homeFragment, false)
            navController.navigate(fragmentId);
        }
    }

}