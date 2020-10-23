package co.deshbidesh.db_android.DBNoteFeature.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.deshbidesh.db_android.DBDatabase.Database.DatabaseConverter.DBNoteLanguageConverter
import java.util.*


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
)