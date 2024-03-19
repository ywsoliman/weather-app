package com.example.weatherapp.alerts

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.weatherapp.R
import com.example.weatherapp.databinding.DialogAlertLayoutBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton


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
            showAlertDialog()
        }

    }

    private fun showAlertDialog() {
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