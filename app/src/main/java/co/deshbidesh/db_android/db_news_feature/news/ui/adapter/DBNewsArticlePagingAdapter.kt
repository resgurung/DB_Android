package co.deshbidesh.db_android.db_news_feature.news.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_news_feature.news.models.DBFeaturedImage
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsCategory
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsResponseItem
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.db_news_feature.news.ui.fragments.DBNewsDetailFragmentDirections
import co.deshbidesh.db_android.db_news_feature.news.ui.fragments.DBNewsListFragmentDirections
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DBNewsArticlePagingAdapter: PagingDataAdapter<ArticleWithCategories, DBNewsArticlePagingAdapter.ArticleViewHolder>(DIFF_CALLBACK) {

    // Private inner note view holder class
    inner class ArticleViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val titleTV: TextView = view.findViewById(R.id.newsTitle)

        private val descTV: TextView = view.findViewById(R.id.newsDescription)

        private val publishedDate: TextView = view.findViewById(R.id.newsDate)

        private val imageView: ImageView = view.findViewById(R.id.newsImage)

        fun bind(articleWithCategory: ArticleWithCategories) {

            titleTV.text = articleWithCategory.article.title

            descTV.text = articleWithCategory.article.description

            publishedDate.text = articleWithCategory.article.published_at

            Glide.with(itemView.context)
                .load(articleWithCategory.article.featured_image.thumbnail)
                .placeholder(R.drawable.ic_loading_image_placeholder)
                .error(R.drawable.ic_loading_broken_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {

        getItem(position)?.let { articleWithCategory ->

            holder.bind(articleWithCategory)

            holder.itemView.setOnClickListener { view ->

                val item = DBNewsResponseItem(
                    post_id = articleWithCategory.article.post_id,
                    author = articleWithCategory.article.author,
                    categories = articleWithCategory.categories.map { DBNewsCategory(it.category_id, it.name) },
                    content = articleWithCategory.article.content,
                    featured_image = articleWithCategory.article.featured_image,
                    format = articleWithCategory.article.format,
                    link = articleWithCategory.article.link,
                    published_at = articleWithCategory.article.published_at,
                    slug = articleWithCategory.article.slug,
                    title = articleWithCategory.article.title,
                    updated_at = articleWithCategory.article.updated_at,
                    description = articleWithCategory.article.description
                )
                val action = DBNewsListFragmentDirections.actionDBNewsListFragmentToNewsDetailFragment(item)

                view.findNavController().navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {

        return ArticleViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.layout_news_list_item, parent, false)
        )
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