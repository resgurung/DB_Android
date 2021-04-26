package co.deshbidesh.db_android.db_settings_feature.viewmodel

import android.content.res.AssetManager
import android.util.JsonReader
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_network.repository.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.models.DBSettingsData
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.*

class DBSettingsViewModel(
        private val dbSettingsRepository: DBSettingsRepository
): ViewModel() {

    var settingsData: MutableLiveData<Response<DBSettingsData>> = MutableLiveData()

    fun getSettingsData(){
        viewModelScope.launch {
            settingsData.value =dbSettingsRepository.getSettingsData()
        }
    }

    fun getSettingsDataTest(manager:AssetManager, listener: (DBSettingsData) -> Unit){

        val gSon = Gson()

        var inputStream: InputStream? = null
        try {
            inputStream = manager.open("dbsettings.json")

            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val dbSettingsData = gSon.fromJson(jsonString, DBSettingsData::class.java)

            listener(dbSettingsData)

        }catch (e: IOException){

            Log.d("Asset", e.toString() )
        }finally {
            inputStream?.close()
        }
    }
}