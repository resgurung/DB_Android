package co.deshbidesh.db_android.db_settings_feature.models

import java.io.Serializable

data class DBSetting(
    val facebook:           String,
    val instagram:          String,
    val legal_disclosure:   String,
    val privacy_policy:     String,
    val subtitle:           String,
    val email:              String,
    val terms_of_use:       String,
    val title:              String,
    val twitter:            String,
    val url:                String,
    val version:            String,
    val youtube:            String
): Serializable