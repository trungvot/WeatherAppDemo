package com.trungvothanh.weatherapplication.alert

import android.content.Context
import androidx.appcompat.app.AlertDialog

class AlertManager {

    fun showToHide(context: Context, title: String, message: String, buttonName: String) {
        val builder = AlertDialog.Builder(context)
        // Set the alert dialog title
        builder.setTitle(title)

        // Display a message on alert dialog
        builder.setMessage(message)

        // Display a  button on alert dialog
        builder.setNeutralButton(buttonName) { dialog, which -> }

        // Make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }
}