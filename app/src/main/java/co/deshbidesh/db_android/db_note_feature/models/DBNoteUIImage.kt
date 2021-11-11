package co.deshbidesh.db_android.db_note_feature.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


enum class ImageInputType {
    CAMERA, GALLERY, DATABASE
}

@Parcelize
data class DBNoteUIImage(
    val id: Int = 0,
    val noteId: Int = 0,
    val imagePath: String = "",
    val doneButton: Boolean = false,
    val saveButton: Boolean = false,
    val cancelButton: Boolean = false,
    val deleteButton: Boolean = false
): Parcelable