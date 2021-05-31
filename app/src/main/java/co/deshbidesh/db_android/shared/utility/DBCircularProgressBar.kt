package co.deshbidesh.db_android.shared.utility

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import co.deshbidesh.db_android.R

class DBCircularProgressBar(
        private val activity: Activity
        ) {

    private lateinit var dialog: AlertDialog

    fun loadAlertDialog() {

        val builder = AlertDialog.Builder(activity)

        builder.setView(activity.layoutInflater.inflate(R.layout.db_circular_progress_bar, null))

        // user cannot dissmiss the dialog
        builder.setCancelable(false)

        dialog = builder.create()

        dialog.show()
    }

    fun hideAlertDialog() {

        dialog.dismiss()
    }
}