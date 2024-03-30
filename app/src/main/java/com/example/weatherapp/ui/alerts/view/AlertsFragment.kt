package com.example.weatherapp.ui.alerts.view

import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DialogAlertLayoutBinding
import com.example.weatherapp.databinding.FragmentAlertsBinding
import com.example.weatherapp.db.WeatherDatabase
import com.example.weatherapp.db.WeatherLocalDataSource
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.network.WeatherRemoteDataSource
import com.example.weatherapp.repository.Repository
import com.example.weatherapp.sharedpref.SharedPrefManager
import com.example.weatherapp.ui.WeatherAnimationViewModel
import com.example.weatherapp.ui.alerts.AndroidAlarmScheduler
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModel
import com.example.weatherapp.ui.alerts.viewmodel.AlertViewModelFactory
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val NOTIFICATION_REQUEST_CODE = 200

class AlertsFragment : Fragment() {

    private lateinit var binding: FragmentAlertsBinding
    private lateinit var alertsAdapter: AlertsAdapter
    private val alarmScheduler by lazy { AndroidAlarmScheduler(requireContext()) }
    private val weatherAnimationViewModel: WeatherAnimationViewModel by activityViewModels()
    private val alertViewModel: AlertViewModel by viewModels {
        AlertViewModelFactory(
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                weatherAnimationViewModel.weatherState.collectLatest {
                    binding.weatherAnimation.setWeatherData(it)
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

    private fun handleDeleteAlertButton(alarmItem: AlarmItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_selected_alert))
            .setMessage(getString(R.string.selected_alert_will_be_permanently_removed_and_you_won_t_get_notified_of_the_weather_details_at_that_time))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                alarmScheduler.cancel(alarmItem)
                alertViewModel.deleteAlarmAlert(alarmItem)
            }
            .show()
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
                    getString(R.string.please_select_both_date_and_time_to_continue),
                    Toast.LENGTH_SHORT
                )
                    .show()
                return@setOnClickListener
            }

            val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.US)
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)
            val localDate = LocalDate.parse(binding.selectDateBtn.text, dateFormatter)
            val localTime = LocalTime.parse(binding.selectTimeBtn.text, timeFormatter)

            val coordinates = SharedPrefManager.getInstance(requireContext()).getCoordinates()
            coordinates?.let {
                val alarmItem =
                    AlarmItem(LocalDateTime.of(localDate, localTime), it.latitude, it.longitude)
                alarmScheduler.schedule(alarmItem)
                alertViewModel.insertAlarmAlert(alarmItem)
            }

            dialog.dismiss()
        }

    }

    private fun openDateDialog(onDateSelected: (String) -> Unit) {

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build()

        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.select_date))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder)
                .build()

        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH)
        datePicker.addOnPositiveButtonClickListener {
            val selectedDate = Date(it)
            val formattedDate = dateFormat.format(selectedDate)
            onDateSelected(formattedDate)
        }

        datePicker.show(requireActivity().supportFragmentManager, "date_dialog")

    }

    private fun openClockDialog(onTimeSelected: (String) -> Unit) {

        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(currentHour)
            .setMinute(currentMinute)
            .setTitleText(getString(R.string.select_time))
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val hour =
                if (timePicker.hour == 0) 12 else if (timePicker.hour > 12) timePicker.hour - 12 else timePicker.hour
            val minute = timePicker.minute
            val amPm = if (timePicker.hour >= 12) "PM" else "AM"
            val formattedTime = String.format(Locale.US, "$hour:$minute $amPm")
            onTimeSelected(formattedTime)
        }

        timePicker.show(requireActivity().supportFragmentManager, "clock_dialog")

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