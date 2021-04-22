package co.deshbidesh.db_android.application

import android.app.Application
import android.util.Log
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.shared.utility.DBDocScanConstant
import co.deshbidesh.db_android.shared.utility.DBPreferenceHelper


class DBApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        DBPreferenceHelper.init(this)

        DBDatabase.getDatabase(this)

        //DBDocScanConstant.setInitialPreferences()

        Log.d("DBApplication", "DBApplication loaded")
    }
}