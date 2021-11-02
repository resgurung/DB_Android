package co.deshbidesh.db_android.db_news_feature.news.presentation.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_news_feature.news.domain.models.*
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.fragments.DBNewsListFragmentDirections
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.views.ArticleViewHolder
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.views.NativeAdViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.gms.ads.nativead.NativeAdView

class DBNewsArticlePagingAdapter: PagingDataAdapter<ArticleWithCategories, RecyclerView.ViewHolder>(
    DIFF_CALLBACK
) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder) {
            is ArticleViewHolder -> {

                getItem(position)?.let { articleWithCategory ->

                    holder.bind(articleWithCategory)

                    holder.itemView.setOnClickListener { view ->

                        val item = DBNewsResponseItem(
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
                            DBNewsListFragmentDirections.actionDBNewsListFragmentToNewsDetailFragment(item)

                        view.findNavController().navigate(action)
                    }
                }
            }
            else -> {
                // something wrong
                Log.d("DBNewsArticlePagingAdapter","Should not be inside else statement onBindViewHolder because we only have one view")
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val v = inflater.inflate(R.layout.layout_news_list_item, parent, false)

        return ArticleViewHolder(v)

    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleWithCategories>() {

            override fun areItemsTheSame(oldItem: ArticleWithCategories, newItem: ArticleWithCategories): Boolean {

                return oldItem.article.n_id == newItem.article.n_id
            }

            override fun areContentsTheSame(oldItem: ArticleWithCategories, newItem: ArticleWithCategories): Boolean {

                return oldItem == newItem
            }
        }

    }
}