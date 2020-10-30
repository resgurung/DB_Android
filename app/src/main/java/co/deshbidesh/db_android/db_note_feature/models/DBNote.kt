package co.deshbidesh.db_android.db_note_feature.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.deshbidesh.db_android.db_database.database.DatabaseConverter.DBNoteLanguageConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "db_notes")
@TypeConverters(DBNoteLanguageConverter::class)
data class DBNote (

    @PrimaryKey(autoGenerate = true)
    val id:             Int,

    val title:          String,

    val description:    String?,

    val content:        String,

    val createdDate:    Date?,

    val updatedDate:    Date?,

    val languageType:   DBNoteLanguage
): Parcelable