package co.deshbidesh.db_android.shared.utility

import android.content.Context
import android.content.SharedPreferences



object DBPreferenceHelper {

    private const val NAME = "DeshBidesh"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences
    // list of app specific preferences
    private val IS_FIRST_RUN_PREF = Pair("is_first_run", true)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit()
    and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation:
                                              (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var firstRun: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = getStoredBoolean(IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second)//preferences.getBoolean(IS_FIRST_RUN_PREF.first, IS_FIRST_RUN_PREF.second)

        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putBoolean(IS_FIRST_RUN_PREF.first, value)
        }

    fun storeInt(key: String, value: Int) {

        preferences.edit {
            it.putInt(key, value)
        }
    }

    fun getStoredInt(key: String, defValue: Int): Int {

        return preferences.getInt(key, defValue)
    }

    fun storeBoolean(key: String, value: Boolean) {

        preferences.edit{
            it.putBoolean(key, value)
        }
    }

    fun getStoredBoolean(key: String, defValue: Boolean): Boolean = preferences.getBoolean(key, defValue)

    fun storeFloat(key: String, value: Float) {

        preferences.edit{
            it.putFloat(key, value)
        }
    }
    // get float
    fun getStoredFloat(key: String, defValue: Float): Float = preferences.getFloat(key, defValue)

    fun storeLong(key: String, value: Long) {

        preferences.edit{
            it.putLong(key, value)
        }
    }

    fun getStoredLong(key: String, defValue: Long): Long = preferences.getLong(key, defValue)

    fun storeString(key: String, value: String) {

        preferences.edit{
            it.putString(key, value)
        }
    }

    fun getStoredString(key: String, defValue: String): String? = preferences.getString(key, defValue)

}