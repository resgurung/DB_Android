package co.deshbidesh.db_android.db_settings_feature.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_network.repository.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.models.DBSettingsData
import kotlinx.coroutines.launch
import retrofit2.Response

class DBSettingsViewModel(
        private val dbSettingsRepository: DBSettingsRepository
): ViewModel() {

    var settingsData: MutableLiveData<Response<DBSettingsData>> = MutableLiveData()

    fun getSettingsData(){
        viewModelScope.launch {
            settingsData.value =dbSettingsRepository.getSettingsData()
        }
    }
}