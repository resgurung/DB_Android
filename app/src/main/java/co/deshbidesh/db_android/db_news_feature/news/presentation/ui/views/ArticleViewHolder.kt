package co.deshbidesh.db_android.db_news_feature.news.presentation.ui.views

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.extensions.loadImage

class ArticleViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val titleTV: TextView = view.findViewById(R.id.newsTitle)

    private val descTV: TextView = view.findViewById(R.id.newsDescription)

    private val catTV: TextView = view.findViewById(R.id.newsCategory)

    private val publishedDate: TextView = view.findViewById(R.id.newsDate)

    private val imageView: ImageView = view.findViewById(R.id.newsImage)

    fun bind(articleWithCategory: ArticleWithCategories) {

        titleTV.text = articleWithCategory.article.title

        descTV.text = articleWithCategory.article.description

        catTV.text = articleWithCategory.categories.first().name

        publishedDate.text = "Published: ${DBHelper.formatDateForNews(articleWithCategory.article.published_at)}"

        imageView.loadImage(articleWithCategory.article.featured_image.thumbnail)
    }
}