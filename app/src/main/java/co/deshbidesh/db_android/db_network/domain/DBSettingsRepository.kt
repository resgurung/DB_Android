package co.deshbidesh.db_android.db_network.domain

import co.deshbidesh.db_android.db_network.db_retrofit.DBRetrofitInstance
import co.deshbidesh.db_android.db_settings_feature.models.DBSetting
import co.deshbidesh.db_android.db_settings_feature.models.DBSettingsData
import retrofit2.Response

class DBSettingsRepository {

    suspend fun getSettingsData(): Response<DBSettingsData>{

        return DBRetrofitInstance.dbSettingsApi.getSettingsData()
    }

    suspend fun getAppSettings(version: Int, response: (Response<DBSetting>) -> Unit)  {

        response(DBRetrofitInstance.dbSettingsApi.getSettings(version))
    }

    suspend fun getSettingsFromNetwork(version: Int): Response<DBSetting> {

        return DBRetrofitInstance.dbSettingsApi.getSettings(version)
    }
}