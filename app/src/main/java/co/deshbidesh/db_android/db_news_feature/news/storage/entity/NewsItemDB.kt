package co.deshbidesh.db_android.db_news_feature.news.storage.entity

import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.models.DBFeaturedImage


@Entity(
    tableName = "articles"
)
data class NewsItemDB(
    @PrimaryKey(autoGenerate = true)
    val n_id: Long = 0L,

    val post_id: Int,

    val title: String,

    val description: String,

    val content: String,

    val author: String,

    @Embedded
    val featured_image: DBFeaturedImage,

    val format: String,

    val link: String,

    val slug: String,

    val published_at: String,

    val updated_at: String
)
