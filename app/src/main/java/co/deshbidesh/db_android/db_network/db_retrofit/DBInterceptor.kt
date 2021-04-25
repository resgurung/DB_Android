package co.deshbidesh.db_android.db_network.db_retrofit

import co.deshbidesh.db_android.db_settings_feature.settings_utils.DBSettingsConstant
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class DBInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
                .newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-api-key", DBSettingsConstant.KEY)
                .build()

        return chain.proceed(request)
    }
}