package co.deshbidesh.db_android.shared.extensions

import android.app.AlertDialog
import androidx.fragment.app.Fragment

fun Fragment.showAlert(message: String) {

    val dialogBuilder = AlertDialog.Builder(activity)

    dialogBuilder.setMessage(message)
            // if the dialog is cancelable
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }

    val alert = dialogBuilder.create()
    alert.setTitle("Error")
    alert.show()
}