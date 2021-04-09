package co.deshbidesh.db_android.shared.extensions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat


fun Activity.hasPermission(vararg permissions: String): Boolean {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        permissions.all { singlePermission ->
            applicationContext.checkSelfPermission(singlePermission) == PackageManager.PERMISSION_GRANTED
        }
    else true
}

fun Activity.askPermission(vararg permissions: String, requestCode: Int) =
        ActivityCompat.requestPermissions(this, permissions, requestCode)