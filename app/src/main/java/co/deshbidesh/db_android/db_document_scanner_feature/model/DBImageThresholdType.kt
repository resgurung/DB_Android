package co.deshbidesh.db_android.db_document_scanner_feature.model

enum class DBImageThresholdType {

    BINARY, ADAPTIVE, OTSU;

    fun toRaw() = enumToRaw[this]

    companion object {

        private val rawToEnum = mapOf(
                0 to BINARY,
                1 to ADAPTIVE,
                2 to OTSU
        )

        val enumToRaw = rawToEnum.entries.associate{(k,v)-> v to k}

        fun ofRaw(raw: Int): DBImageThresholdType? = rawToEnum[raw]
    }
}