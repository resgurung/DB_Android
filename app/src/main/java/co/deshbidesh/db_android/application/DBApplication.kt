package co.deshbidesh.db_android.application

import android.app.Application
import co.deshbidesh.db_android.shared.utility.DBDocScanConstant
import co.deshbidesh.db_android.shared.utility.DBPreferenceHelper


class DBApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        DBDocScanConstant.setInitialPreferences()
    }
}