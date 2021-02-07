package co.deshbidesh.db_android.db_database.database.database_converter

import androidx.room.TypeConverter
import java.util.*

class DBDateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}