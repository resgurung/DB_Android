package co.deshbidesh.db_android.db_document_scanner_feature.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DBDocScanURI(
    var uri: Uri
) : Parcelable

