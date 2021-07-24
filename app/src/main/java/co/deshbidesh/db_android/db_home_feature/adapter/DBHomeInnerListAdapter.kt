package co.deshbidesh.db_android.db_home_feature.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsCategory
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsResponseItem
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsUiModel
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.db_news_feature.news.ui.fragments.DBNewsListFragmentDirections
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DBHomeInnerListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Private inner note view holder class
    inner class InnerArticleViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val titleTV: TextView = view.findViewById(R.id.newsTitle)

        private val descTV: TextView = view.findViewById(R.id.newsDescription)

        private val publishedDate: TextView = view.findViewById(R.id.newsDate)

        private val imageView: ImageView = view.findViewById(R.id.newsImage)

        fun bind(uiModel: DBNewsUiModel) {

            titleTV.text = uiModel.title

            descTV.text = uiModel.desc

            publishedDate.text = uiModel.publishedAt

            Glide.with(itemView.context)
                .load(uiModel.thumbnail)
                .placeholder(R.drawable.ic_loading_image_placeholder)
                .error(R.drawable.ic_loading_broken_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }

    private var newsItems: List<DBNewsUiModel> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_home_news_list_item, parent, false)

        itemView.post {

            itemView.layoutParams.height = (parent.measuredHeight - 72) / 3

            itemView.requestLayout()
        }

        return InnerArticleViewHolder (itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {

            is InnerArticleViewHolder -> {

                holder.bind(newsItems[position])

//                holder.itemView.setOnClickListener { view ->

//                    val articleWithCategory = newsItems[position]
//
//                    val item = DBNewsResponseItem(
//                        post_id = articleWithCategory.article.post_id,
//                        author = articleWithCategory.article.author,
//                        categories = articleWithCategory.categories.map { DBNewsCategory(it.category_id, it.name) },
//                        content = articleWithCategory.article.content,
//                        featured_image = articleWithCategory.article.featured_image,
//                        format = articleWithCategory.article.format,
//                        link = articleWithCategory.article.link,
//                        published_at = articleWithCategory.article.published_at,
//                        slug = articleWithCategory.article.slug,
//                        title = articleWithCategory.article.title,
//                        updated_at = articleWithCategory.article.updated_at,
//                        description = articleWithCategory.article.description
//                    )
//                    val action = DBNewsListFragmentDirections.actionDBNewsListFragmentToNewsDetailFragment(item)
//
//                    view.findNavController().navigate(action)
//                }
            }
        }
    }

    override fun getItemCount(): Int {

        return 3
    }

    fun loadData(newsItems: List<DBNewsUiModel>) {

        this.newsItems = newsItems

        notifyDataSetChanged()
    }
}