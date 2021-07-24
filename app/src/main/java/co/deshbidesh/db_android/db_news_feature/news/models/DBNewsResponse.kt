package co.deshbidesh.db_android.db_news_feature.news.models

import java.io.Serializable

class DBNewsResponse: ArrayList<DBNewsResponseItem>()

data class DBNewsResponseItem(
    val post_id: Int,

    val author: String,

    val categories: List<DBNewsCategory>,

    val content: String,

    val featured_image: DBFeaturedImage,

    val format: String,

    val link: String,

    val published_at: String,

    val slug: String,

    val title: String,

    val updated_at: String,

    val description: String

): Serializable