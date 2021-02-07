package co.deshbidesh.db_android.db_database.database.database_converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DBImageIdListConverter {

    @TypeConverter
    fun fromStringToArrayList(value: String?): ArrayList<String>? {
        val listType = object: TypeToken<ArrayList<String>>(){}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayListToString(list: ArrayList<String>?): String? {
        val gSon = Gson()
        return gSon.toJson(list)
    }
}