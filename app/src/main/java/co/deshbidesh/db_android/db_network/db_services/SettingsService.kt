package co.deshbidesh.db_android.db_network.db_services

import co.deshbidesh.db_android.db_settings_feature.models.DBSetting
import co.deshbidesh.db_android.db_settings_feature.models.DBSettingsData
import co.deshbidesh.db_android.shared.utility.DBSettingConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SettingsService {

    @GET("/v1/m1/setting/data")
    suspend fun getSettingsData(): Response<DBSettingsData>


    /*

    GET URL parameter

        Version (setting version)
        key: v
        value: 1/2/3/3....n
        cannot be empty and has to be number.

     */
    @GET("db/v1/settings")
    suspend fun getSettings(
        @Query(DBSettingConstants.VERSION)
        version: Int = 0
    ): Response<DBSetting>
}