package co.deshbidesh.db_android.db_document_scanner_feature.model

import android.graphics.Bitmap
import android.os.Parcelable
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.SharedViewModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DBDocScanSaveObject(
        val route: SharedViewModel.Route,
        val bitmap: Bitmap
        ): Parcelable
