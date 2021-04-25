package co.deshbidesh.db_android.db_settings_feature.models

import java.io.Serializable

data class DBData(
        val ytURL: String,
        val contactPhNumber: String,
        val companyInfo: String,
        val legalDisclaimer: String,
        val companyName: String,
        val contactName: String,
        val privacy: String,
        val ttURL: String,
        val position: String,
        val companyId: String,
        val contactEmail: String,
        val tncs: String,
        val igURL: String,
        val fbURL: String,
        val id: Int,
        val companyLogo: String
): Serializable