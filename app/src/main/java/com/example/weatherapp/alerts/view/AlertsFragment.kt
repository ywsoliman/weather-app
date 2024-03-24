package com.example.weatherapp.alerts.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.alerts.AndroidAlarmScheduler
import com.example.weatherapp.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.alerts.viewmodel.AlertViewModelFactory
import com.example.weatherapp.databinding.DialogAlertLayoutBinding
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.models.Repository
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

private const val NOTIFICATION_REQUEST_CODE = 200
private const val TAG = "AlertsFragment"

class AlertsFragment : Fragment() {

    private lateinit var binding: FragmentAlertsBinding
    private lateinit var alertsAdapter: AlertsAdapter
    private val alarmScheduler by lazy {
        AndroidAlarmScheduler(requireContext())
    }
    private val alertViewModel: AlertViewModel by viewModels {
        AlertViewModelFactory(
            Repository.getInstance(
                WeatherLocalDataSource(requireContext()),
                WeatherRemoteDataSource
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alerts, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        alertsAdapter = AlertsAdapter {
            handleDeleteAlertButton(it)
        }

        binding.adapter = alertsAdapter

        binding.addAlertBtn.setOnClickListener {
            handleAlertFAB()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                alertViewModel.alerts.collectLatest {
                    if (it.isEmpty()) {
                        alertsAdapter.submitList(emptyList())
                        binding.noAlertsImage.visibility = View.VISIBLE
                        binding.noAlertsText.visibility = View.VISIBLE
                    } else {
                        binding.noAlertsImage.visibility = View.GONE
                        binding.noAlertsText.visibility = View.GONE
                        alertsAdapter.submitList(it)
                    }
                }
            }
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


        binding.selectDateBtn.setOnClickListener {
            openDateDialog { date ->
                binding.selectDateBtn.text = date
            }
        }

        binding.selectTimeBtn.setOnClickListener {
            openClockDialog { date ->
                binding.selectTimeBtn.text = date
            }
        }

        binding.alertSaveBtn.setOnClickListener {

            if (binding.selectDateBtn.text.equals(getString(R.string.press_to_select_a_date)) ||
                binding.selectTimeBtn.text.equals(getString(R.string.press_to_select_a_time))
            ) {
                Toast.makeText(
                    requireContext(),
                    "Please select both date and time to continue",
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val dateFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH)
            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
            val localDate = LocalDate.parse(binding.selectDateBtn.text, dateFormatter)
            val localTime = LocalTime.parse(binding.selectTimeBtn.text, timeFormatter)

            val alarmItem = AlarmItem(LocalDateTime.of(localDate, localTime))

            alarmScheduler.schedule(alarmItem)
            alertViewModel.insertAlarmAlert(alarmItem)

            dialog.dismiss()
        }

    }

    private fun handleDeleteAlertButton(it: AlarmItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_selected_alert))
            .setMessage(getString(R.string.selected_alert_will_be_permanently_removed_and_you_won_t_get_notified_of_the_weather_details_at_that_time))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                alertViewModel.deleteAlarmAlert(it)
            }
            .show()
    }

    private fun openDateDialog(onDateSelected: (String) -> Unit) {

        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

        val dialog = DatePickerDialog(requireContext(), { _, pickedYear, pickedMonth, pickedDay ->

            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(pickedYear, pickedMonth, pickedDay)

            val dateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedCalendar.time)

            onDateSelected(formattedDate)

        }, year, month, dayOfMonth)

        dialog.datePicker.minDate = System.currentTimeMillis()
        dialog.show()
    }

    private fun openClockDialog(onTimeSelected: (String) -> Unit) {
        val time = Calendar.getInstance().time
        val dialog = TimePickerDialog(requireContext(), { _, pickedHour, pickedMinute ->

            time.hours = pickedHour
            time.minutes = pickedMinute
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val formattedTime = timeFormat.format(time)
            onTimeSelected(formattedTime)

        }, time.hours, time.minutes, false)

        dialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            handleAddingAlert()
        }
    }

}