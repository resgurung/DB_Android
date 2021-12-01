package co.deshbidesh.db_android.shared

import android.content.Context
import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import co.deshbidesh.db_android.shared.extensions.isDarkThemeOn
import co.deshbidesh.db_android.shared.utility.DBPermissionConstants
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DBHelper {

    companion object {

        val cameraPermissions = arrayOf(DBPermissionConstants.CameraPermission)

        val externalStoragePermissions = arrayOf(DBPermissionConstants.ReadExternalStorage)


        // helper fun to generate description from content
        fun generateDescriptionFromContent(content: String): String {

            var stringArray: List<String> = content.split(" ")

            return if (stringArray.count() > 15) {

                stringArray.take(15).joinToString(separator = " ") { it ->
                    "$it"
                }
            } else {

                stringArray.joinToString(separator = " ") { it ->
                    "$it"
                }
            }
        }

        fun getFirstNStrings(str: String, n: Int): String? {
            val sArr = str.split(" ").toTypedArray()
            var firstStrs = ""
            for (i in 0 until n) firstStrs += sArr[i] + " "
            return firstStrs.trim { it <= ' ' }
        }

        // date formatter for news published at date from string ISO_OFFSET_DATE_TIME to dd/MM/yyyy HH:mma
        fun formatDateForNews(publishedAt: String): String {

            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mma")

            val dateTime = OffsetDateTime.parse(publishedAt)

            return formatter.format(dateTime)
        }

        fun formatDateForNote(date: Date?): String {

            val pattern1 = "EEEE, dd-MMM-yyyy hh:mm a"
            val pattern2 = "dd MMM yyyy"
            val simpleDateFormat = SimpleDateFormat(pattern2)

            return simpleDateFormat.format(date)
        }

        // web dark mode or light mode
        fun webSettingForDarkMode(context: Context, webView: WebView) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {

                if (context.isDarkThemeOn()) {

                    WebSettingsCompat.setForceDark(
                        webView.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )

                } else {

                    WebSettingsCompat.setForceDark(
                        webView.settings,
                        WebSettingsCompat.FORCE_DARK_OFF
                    )
                }
            }
        }

        fun requestAdmobBanner(view: AdView) {
            val adMobRequest = AdRequest
                .Builder()
                .build()

            view.loadAd(adMobRequest)
        }
    }
}