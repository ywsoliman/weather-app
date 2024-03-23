package com.example.weatherapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.weatherapp.cachemanager.CacheWorker
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.mapFragment)
                bottomNavigationView.visibility = View.GONE
            else
                bottomNavigationView.visibility = View.VISIBLE
        }

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

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onDestroy: ")
        val workManager = WorkManager.getInstance(this)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<CacheWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(request)
    }

}