package co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations

import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.CategoryDB
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.NewsItemDB


@Entity(
    primaryKeys = ["n_id", "c_id"]
)
data class ArticleCategoryJoin(
    val n_id: Long, //article
    val c_id: Long // category
)


data class ArticleWithCategories(
    @Embedded val article: NewsItemDB,
    @Relation(
        parentColumn = "n_id",
        entityColumn = "c_id",
        associateBy = Junction(ArticleCategoryJoin::class)
    )
    val categories: List<CategoryDB>
)

data class CategoryWithArticles(
    @Embedded val category: CategoryDB,
    @Relation(
        parentColumn = "c_id",
        entityColumn = "n_id",
        associateBy = Junction(ArticleCategoryJoin::class)
    )
    val articles: List<NewsItemDB>
)
