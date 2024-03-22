package com.example.weatherapp.alerts

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DialogAlertLayoutBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


private const val NOTIFICATION_REQUEST_CODE = 200

class AlertsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alerts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: FloatingActionButton = view.findViewById(R.id.addAlertBtn)
        fab.setOnClickListener {
            handleAlertFAB()
        }

    }

    private fun handleAlertFAB() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationManager =
                requireContext().getSystemService(NotificationManager::class.java)
            if (notificationManager.areNotificationsEnabled())
                handleAddingAlert()
            else {
                requestPermissions(
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_REQUEST_CODE
                )
            }
        } else
            handleAddingAlert()
    }

    private fun handleAddingAlert() {

        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        val binding: DialogAlertLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_alert_layout,
            null,
            false
        )
        builder.setView(binding.root)

        val dialog: Dialog = builder.create()
        dialog.show()

    }

}