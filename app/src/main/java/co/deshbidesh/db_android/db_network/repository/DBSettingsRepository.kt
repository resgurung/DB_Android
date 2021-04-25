package co.deshbidesh.db_android.db_network.repository

import co.deshbidesh.db_android.db_network.db_retrofit.DBRetrofitInstance
import co.deshbidesh.db_android.db_settings_feature.models.DBSettingsData
import retrofit2.Response

class DBSettingsRepository {

    suspend fun getSettingsData(): Response<DBSettingsData>{
        return DBRetrofitInstance.dbApi.getSettingsData()
    }
}