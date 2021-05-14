package co.deshbidesh.db_android.shared.extensions

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.deshbidesh.db_android.R

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
    val button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
    with(button) {
        setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
    }
}