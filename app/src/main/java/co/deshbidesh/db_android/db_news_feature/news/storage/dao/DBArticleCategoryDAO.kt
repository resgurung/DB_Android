package co.deshbidesh.db_android.db_news_feature.news.storage.dao


import androidx.paging.PagingSource
import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleCategoryJoin
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.CategoryWithArticles


@Dao
interface DBArticleCategoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(articleCategoryJoin: ArticleCategoryJoin): Long

    @Transaction
    @Query("SELECT * FROM articles ORDER BY published_at DESC")
    fun getArticlesWithCategories(): PagingSource<Int, ArticleWithCategories>//List<ArticleWithCategories>

    @Transaction
    @Query("SELECT * FROM articles ORDER BY published_at DESC LIMIT 3")
    suspend fun getHomeArticlesWithCategories(): List<ArticleWithCategories>

    @Transaction
    @Query("SELECT * FROM categories")
    suspend fun getCategoriesWithArticles(): List<CategoryWithArticles>

    @Delete
    suspend fun delete(join: ArticleCategoryJoin)

    @Query("DELETE FROM ArticleCategoryJoin")
    suspend fun deleteAll()

    @Query("DELETE FROM ArticleCategoryJoin WHERE ArticleCategoryJoin.n_id =:articleId")
    suspend fun removeAllArticleCategoryJoinByArticleId( articleId: Long)

    @Query("DELETE FROM ArticleCategoryJoin WHERE ArticleCategoryJoin.c_id =:categoryId")
    suspend fun removeAllArticleCategoryJoinByCategoryId( categoryId : Long)
}