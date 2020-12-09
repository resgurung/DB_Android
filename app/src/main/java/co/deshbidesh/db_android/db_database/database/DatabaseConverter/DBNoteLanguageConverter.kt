package co.deshbidesh.db_android.db_database.database.DatabaseConverter


import androidx.room.TypeConverter
import co.deshbidesh.db_android.db_note_feature.models.DBNoteLanguage

class DBNoteLanguageConverter {

    @TypeConverter
    fun fromInteger(value: Int): DBNoteLanguage {

        return when (value) {

            0 -> DBNoteLanguage.ENGLISH

            1 -> DBNoteLanguage.NEPALI

            else -> DBNoteLanguage.UNKNOWN
        }
    }

    @TypeConverter
    fun languageToInteger(language: DBNoteLanguage): Int {

        return language.code
    }
}