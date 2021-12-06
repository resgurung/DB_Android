package co.deshbidesh.db_android.db_news_feature.news.domain.models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.fragments.DBNewsListFragmentDirections
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.views.ArticleViewHolder
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.views.NativeAdViewHolder
import com.google.android.gms.ads.nativead.NativeAd


sealed class MenuNewsAdListModel(open val identifier: String)

class NewsNativeAd(
    override val identifier: String,
    val ad: NativeAd
    ): MenuNewsAdListModel(identifier)

class DataElement(
    override val identifier: String,
    val item: ArticleWithCategories
    ): MenuNewsAdListModel(identifier)

class ArticlePagerAdapter : PagingDataAdapter<MenuNewsAdListModel, RecyclerView.ViewHolder>(
    DIFF_CALLBACK
) {

    //var adLoaded: Boolean = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view: View
        return when (viewType) {
            ITEM_VIEW -> {
                view = inflater.inflate(
                    co.deshbidesh.db_android.R.layout.layout_news_list_item,
                    parent,
                    false
                )

                ArticleViewHolder(view)

            }
            AD_VIEW -> {

                view = inflater.inflate(
                    co.deshbidesh.db_android.R.layout.unified_native_ad_view,
                    parent,
                    false
                )

                NativeAdViewHolder(view)
            }
            else -> throw IllegalArgumentException("Unknown Holder type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        getItem(position)?.let { item ->

            when (item) {

                is DataElement -> {

                    val articleWithCategory = item.item

                    (holder as ArticleViewHolder).bind(articleWithCategory)

                    holder.itemView.setOnClickListener { view ->

                        val responseItem = DBNewsResponseItem(
                            post_id = articleWithCategory.article.post_id,
                            author = articleWithCategory.article.author,
                            categories = articleWithCategory.categories.map {
                                DBNewsCategory(
                                    it.category_id,
                                    it.name
                                )
                            },
                            content = articleWithCategory.article.content,
                            featured_image = articleWithCategory.article.featured_image,
                            format = articleWithCategory.article.format,
                            link = articleWithCategory.article.link,
                            published_at = articleWithCategory.article.published_at,
                            slug = articleWithCategory.article.slug,
                            title = articleWithCategory.article.title,
                            updated_at = articleWithCategory.article.updated_at,
                            description = articleWithCategory.article.description,
                            featured = articleWithCategory.article.featured,
                            videoURL = articleWithCategory.article.videoURL
                        )
                        val action =
                            DBNewsListFragmentDirections.actionDBNewsListFragmentToNewsDetailFragment(
                                responseItem
                            )

                        view.findNavController().navigate(action)
                    }
                }
                is NewsNativeAd -> {

                    (holder as NativeAdViewHolder).getAdView().setNativeAd(item.ad)
                }
            }
        }
//
//        when (holder) {
//            is ArticleViewHolder -> {
//
//                getItem(position)?.let { item ->
//
//                    when (item) {
//
//                        is DataElement -> {
//
//                            val articleWithCategory = item.item
//
//                            holder.bind(articleWithCategory)
//
//                            holder.itemView.setOnClickListener { view ->
//
//                                val responseItem = DBNewsResponseItem(
//                                    post_id = articleWithCategory.article.post_id,
//                                    author = articleWithCategory.article.author,
//                                    categories = articleWithCategory.categories.map {
//                                        DBNewsCategory(
//                                            it.category_id,
//                                            it.name
//                                        )
//                                    },
//                                    content = articleWithCategory.article.content,
//                                    featured_image = articleWithCategory.article.featured_image,
//                                    format = articleWithCategory.article.format,
//                                    link = articleWithCategory.article.link,
//                                    published_at = articleWithCategory.article.published_at,
//                                    slug = articleWithCategory.article.slug,
//                                    title = articleWithCategory.article.title,
//                                    updated_at = articleWithCategory.article.updated_at,
//                                    description = articleWithCategory.article.description,
//                                    featured = articleWithCategory.article.featured,
//                                    videoURL = articleWithCategory.article.videoURL
//                                )
//                                val action =
//                                    DBNewsListFragmentDirections.actionDBNewsListFragmentToNewsDetailFragment(
//                                        responseItem
//                                    )
//
//                                view.findNavController().navigate(action)
//                            }
//                        }
//                        else -> {
//                        }
//                    }
//
//                }
//            }
//            is NativeAdViewHolder -> {
//                if (adLoaded) {
//
////                    getAd()?.let { ad ->
////
////                        holder.getAdView().setNativeAd(ad)
////                    }
//                }
//            }
//            else -> {
//            }
//        }
    }

    override fun getItemViewType(position: Int): Int {

        getItem(position)?.let { item ->

            return when (item) {
                is DataElement -> ITEM_VIEW
                is NewsNativeAd -> AD_VIEW
            }

        } ?: run {

            return ITEM_VIEW
        }
    }


//    override fun getItemCount(): Int {
//
//        return super.getItemCount()
////        return if (adLoaded) {
////            var itemCount = super.getItemCount()
////
////            return itemCount + 1
//////            (super.getItemCount() //Number of messages
//////                    + super.getItemCount() / 4 + 1) // Number of ads displayed the plus 1 is for the first element which will be an Ad
////
////        } else {
////
////            super.getItemCount()
////        }
//    }


        companion object {

            private val ITEM_VIEW: Int = 0

            private val AD_VIEW: Int = 1

            private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MenuNewsAdListModel>() {

                override fun areItemsTheSame(oldItem: MenuNewsAdListModel, newItem: MenuNewsAdListModel): Boolean {

                    //return oldItem.article.n_id == newItem.article.n_id
                    return oldItem.identifier == newItem.identifier
                }

                override fun areContentsTheSame(oldItem: MenuNewsAdListModel, newItem: MenuNewsAdListModel): Boolean {

                    return oldItem == newItem
                }
            }
        }


//    private fun getAd(): NativeAd? {
//
//        return if (nativeAds.isEmpty() ){
//            null
//        } else {
//            nativeAds.first()
//        }
//    }
}
