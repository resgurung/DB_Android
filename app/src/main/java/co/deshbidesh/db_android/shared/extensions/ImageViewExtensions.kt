package co.deshbidesh.db_android.shared.extensions

import android.widget.ImageView
import co.deshbidesh.db_android.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

fun ImageView.loadImage(url: String) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.ic_loading_image_placeholder)
        .error(R.drawable.ic_baseline_error_48)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}