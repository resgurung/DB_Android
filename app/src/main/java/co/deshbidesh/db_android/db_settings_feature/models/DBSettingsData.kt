package co.deshbidesh.db_android.db_settings_feature.models

import java.io.Serializable

data class DBSettingsData(

    val data: List<DBData>,
    val type: String

): Serializable