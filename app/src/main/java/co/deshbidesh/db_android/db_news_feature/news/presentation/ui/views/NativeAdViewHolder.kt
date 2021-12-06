package co.deshbidesh.db_android.db_news_feature.news.presentation.ui.views


import android.view.View
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import com.google.android.gms.ads.nativead.*


internal class NativeAdViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {
    private val adView: NativeAdView = view.findViewById<View>(R.id.native_ad_view) as NativeAdView

    fun getAdView(): NativeAdView {
        return adView
    }

    init {

        // The MediaView will display a video asset if one is present in the ad, and the
        // first image asset otherwise.
        adView.mediaView = adView.findViewById(R.id.ad_media) as MediaView

        // Register the view used for each individual asset.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
    }

}