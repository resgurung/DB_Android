package co.deshbidesh.db_android.db_network.db_retrofit

import co.deshbidesh.db_android.db_network.db_services.SettingsService
import co.deshbidesh.db_android.db_settings_feature.settings_utils.DBSettingsConstant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object DBRetrofitInstance {

    private val client = OkHttpClient.Builder().apply {
        addInterceptor(DBInterceptor())
    }.build()

    // Singleton
    private val dbRetrofit by lazy {
        Retrofit.Builder()
                .baseUrl(DBSettingsConstant.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val dbApi: SettingsService by lazy {
        dbRetrofit.create(SettingsService::class.java)
    }
}