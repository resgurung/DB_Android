package co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "news_remote_key"
)
data class NewsRemoteKey(
    @PrimaryKey
    val r_id: Long,
    val prev: Int?,
    val next: Int?
)
