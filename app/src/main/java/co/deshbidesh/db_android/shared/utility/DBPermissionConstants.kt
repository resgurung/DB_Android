package co.deshbidesh.db_android.shared.utility

import android.Manifest

class DBPermissionConstants {

    companion object {

        const val cameraPermissionCode = 200

        const val CameraPermission = Manifest.permission.CAMERA

        const val readExternalStoragePermissionCode = 201

        const val ReadExternalStorage = Manifest.permission.READ_EXTERNAL_STORAGE

        const val writeExternalStoragePermissionCode = 202

        const val WriteExternalStorage = Manifest.permission.WRITE_EXTERNAL_STORAGE
    }
}