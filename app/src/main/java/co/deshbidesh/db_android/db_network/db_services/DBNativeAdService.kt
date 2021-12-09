package co.deshbidesh.db_android.db_network.db_services

import android.content.Context
import co.deshbidesh.db_android.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions

class DBNativeAdService {

    companion object {

        private val nativeAds = mutableListOf<NativeAd>()

        fun loadAds(context: Context) {
            if (isNativeAdsArrayEmpty()) {

                val adLoader = AdLoader.Builder(
                    context,
                    R.string.news_list_screen_banner_id.toString()
                )
                    .forNativeAd { ad: NativeAd ->
                        // Show the ad.
                        nativeAds.add(ad)

                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(
                        NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build()
                    )
                    .build()

                adLoader.loadAds(AdRequest.Builder().build(), R.integer.number_of_native_ads)
            }
        }

        fun getAd(index: Int): NativeAd? {

            return if (index >= nativeAds.size) {
                //index not exists
                null
            } else {
                // index exists
                nativeAds[index]
            }
        }

        fun getFirst(): NativeAd? = nativeAds.first()

        fun getCount(): Int = nativeAds.count()

        fun isNativeAdsArrayEmpty(): Boolean = nativeAds.isEmpty()
    }

}