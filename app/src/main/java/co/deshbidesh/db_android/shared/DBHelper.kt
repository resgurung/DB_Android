package co.deshbidesh.db_android.shared

import android.content.Context
import android.webkit.WebView
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import co.deshbidesh.db_android.shared.extensions.isDarkThemeOn
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class DBHelper {

    companion object {

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

        // date formatter for news published at date from string ISO_OFFSET_DATE_TIME to dd/MM/yyyy HH:mma
        fun formatDateForNews(publishedAt: String): String {

            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mma")

            val dateTime = OffsetDateTime.parse(publishedAt)

            return formatter.format(dateTime)
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