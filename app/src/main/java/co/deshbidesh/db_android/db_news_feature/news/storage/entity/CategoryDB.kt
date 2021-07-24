package co.deshbidesh.db_android.db_news_feature.news.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryDB(
    @PrimaryKey(autoGenerate = true)
    val c_id: Long = 0L,

    val category_id: Int,

    val name: String
)