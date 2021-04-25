package co.deshbidesh.db_android.db_network.db_services

import co.deshbidesh.db_android.db_settings_feature.models.DBSettingsData
import retrofit2.Response
import retrofit2.http.GET

interface SettingsService {

    @GET("/v1/m1/setting/data")
    suspend fun getSettingsData(): Response<DBSettingsData>
}