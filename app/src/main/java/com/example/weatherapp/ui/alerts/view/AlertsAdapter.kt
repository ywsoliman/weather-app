package com.example.weatherapp.ui.alerts.view

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.models.AlarmItem
import com.example.weatherapp.databinding.ItemAlarmAlertBinding

class AlertsAdapter(private val onDeleteAlarm: (AlarmItem) -> Unit) :
    ListAdapter<AlarmItem, AlertsAdapter.AlarmViewHolder>(AlarmDiffUtil()) {

    private lateinit var binding: ItemAlarmAlertBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmViewHolder {

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_alarm_alert,
            parent,
            false
        )

        return AlarmViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val currentAlert = getItem(position)
        binding.alarmItem = currentAlert
    }

    inner class AlarmViewHolder(binding: ItemAlarmAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.findViewById<ImageButton>(R.id.deleteBtn).setOnClickListener {
                onDeleteAlarm(getItem(adapterPosition))
            }
        }

    }

    class AlarmDiffUtil : DiffUtil.ItemCallback<AlarmItem>() {
        override fun areItemsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: AlarmItem, newItem: AlarmItem): Boolean {
            return oldItem == newItem
        }

    }

}