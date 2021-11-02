package co.deshbidesh.db_android.db_news_feature.news.domain.storage.dao


import androidx.paging.PagingSource
import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleCategoryJoin
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.CategoryWithArticles
import kotlinx.coroutines.flow.Flow


@Dao
interface DBArticleCategoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(articleCategoryJoin: ArticleCategoryJoin): Long

    @Transaction
    @Query("SELECT * FROM articles ORDER BY published_at DESC")
    fun getArticlesWithCategories(): PagingSource<Int, ArticleWithCategories>

    @Transaction
    @Query("SELECT * FROM articles WHERE featured = :featured ORDER BY published_at DESC")
    fun getHomeArticlesWithCategories(featured: Boolean): Flow<List<ArticleWithCategories>>

    @Transaction
    @Query("SELECT * FROM categories")
    fun getCategoriesWithArticles(): List<CategoryWithArticles>

    @Delete
    fun delete(join: ArticleCategoryJoin)

    @Query("DELETE FROM ArticleCategoryJoin")
    fun deleteAll()

    @Query("DELETE FROM ArticleCategoryJoin WHERE ArticleCategoryJoin.n_id =:articleId")
    fun removeAllArticleCategoryJoinByArticleId( articleId: Long)

    @Query("DELETE FROM ArticleCategoryJoin WHERE ArticleCategoryJoin.c_id =:categoryId")
    fun removeAllArticleCategoryJoinByCategoryId( categoryId : Long)
}