package co.deshbidesh.db_android.db_settings_feature.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_network.repository.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.viewmodel.DBSettingsViewModel

class DBSettingsViewModelFactory(
        private val dbSettingsRepository: DBSettingsRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        return DBSettingsViewModel(dbSettingsRepository) as T

    }
}