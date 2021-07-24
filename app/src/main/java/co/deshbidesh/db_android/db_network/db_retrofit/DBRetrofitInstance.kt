package co.deshbidesh.db_android.db_network.db_retrofit

import co.deshbidesh.db_android.db_network.db_services.DBNewsService
import co.deshbidesh.db_android.db_network.db_services.SettingsService
import co.deshbidesh.db_android.shared.utility.DBApiConstants
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
                .baseUrl(DBApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    val dbSettingsApi: SettingsService by lazy {
        dbRetrofit.create(SettingsService::class.java)
    }

    val dbNewsApi: DBNewsService by lazy {
        dbRetrofit.create(DBNewsService::class.java)
    }
}