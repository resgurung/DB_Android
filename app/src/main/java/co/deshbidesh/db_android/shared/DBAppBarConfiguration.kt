package co.deshbidesh.db_android.shared

import androidx.navigation.ui.AppBarConfiguration
import co.deshbidesh.db_android.R

object DBAppBarConfiguration {

    fun configuration(): AppBarConfiguration {

        return AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.calendarFragment,
            R.id.dictionaryFragment,
            R.id.DBSettingsFragment
        ))
    }
}