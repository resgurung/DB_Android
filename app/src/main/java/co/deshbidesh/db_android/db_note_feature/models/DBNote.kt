package co.deshbidesh.db_android.db_note_feature.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.deshbidesh.db_android.db_database.database.database_converter.DBNoteLanguageConverter
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "db_notes")
@TypeConverters(DBNoteLanguageConverter::class)
data class DBNote (

    @PrimaryKey(autoGenerate = true)
    val id:             Int,

    var title:          String,

    var description:    String,

    var content:        String,

    var imageIds:       ArrayList<String>?,

    val createdDate:    Date?,

    var updatedDate:    Date?,

    val languageType:   DBNoteLanguage

): Parcelable