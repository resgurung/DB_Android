package co.deshbidesh.db_android.db_news_feature.news.storage.dao


import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.storage.entity.NewsItemDB


@Dao
interface DBArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(article: NewsItemDB): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<NewsItemDB>)

    @Delete
    suspend fun delete(article: NewsItemDB)

    @Query("DELETE FROM articles")
    suspend fun deleteAll()
}