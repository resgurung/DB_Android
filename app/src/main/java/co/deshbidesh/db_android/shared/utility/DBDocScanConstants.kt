package co.deshbidesh.db_android.shared.utility

class DBDocScanConstants {

    companion object {

        const val DBIMAGE_PROCESSING_KEY = "DBImageProcessingY"
        const val DBIMAGE_PROCESSING_DEFAULT_VALUE = 1

        const val IS_BINARY_KEY   = "IsBinary"
        const val IS_BINARY_VALUE = false
        const val BINARY_THRESHOLD_SETTING_KEY = "BinaryThresholdSettingKey"
        const val BINARY_THRESHOLD_SETTING_VALUE = 20f // 0 to 255

        const val OTSU_MIN_KEY = "OtsuMinKey"
        const val OTSU_MIN_VALUE = 15f // min is 0

        const val OTSU_MAX_KEY = "OtsuMaxKey"
        const val OTSU_MAX_VALUE = 220f // max is 255

        const val ADAPTIVE_BLOCKSIZE_KEY = "AdaptiveBlockSizeKey"
        const val ADAPTIVE_BLOCKSIZE_DEFAULT_VALUE = 11

        // Constant subtracted from the mean or weighted mean
        const val ADAPTIVE_OFFSET_FROM_MEAN_KEY = "AdaptiveOffsetFromMean"
        const val ADAPTIVE_OFFSET_FROM_MEAN_DEFAULT_VALUE = 2.0f

        const val IS_FIRST_RUN = "isFirstRun"


        fun setInitialPreferences() {

            if (!DBPreferenceHelper.firstRun) {

                DBPreferenceHelper.firstRun = true

                DBPreferenceHelper.storeFloat(
                    DBDocScanConstants.BINARY_THRESHOLD_SETTING_KEY,
                    DBDocScanConstants.BINARY_THRESHOLD_SETTING_VALUE)

                DBPreferenceHelper.storeFloat(
                    DBDocScanConstants.OTSU_MIN_KEY,
                    DBDocScanConstants.OTSU_MIN_VALUE)

                DBPreferenceHelper.storeFloat(
                    DBDocScanConstants.OTSU_MAX_KEY,
                    DBDocScanConstants.OTSU_MAX_VALUE)

                DBPreferenceHelper.storeBoolean(
                    DBDocScanConstants.IS_BINARY_KEY,
                    DBDocScanConstants.IS_BINARY_VALUE)

                DBPreferenceHelper.storeInt(
                    DBDocScanConstants.DBIMAGE_PROCESSING_KEY,
                    DBDocScanConstants.DBIMAGE_PROCESSING_DEFAULT_VALUE)

                DBPreferenceHelper.storeInt(
                    DBDocScanConstants.ADAPTIVE_BLOCKSIZE_KEY,
                    DBDocScanConstants.ADAPTIVE_BLOCKSIZE_DEFAULT_VALUE)

                DBPreferenceHelper.storeFloat(
                    DBDocScanConstants.ADAPTIVE_OFFSET_FROM_MEAN_KEY,
                    DBDocScanConstants.ADAPTIVE_OFFSET_FROM_MEAN_DEFAULT_VALUE)

            }
        }
    }
}