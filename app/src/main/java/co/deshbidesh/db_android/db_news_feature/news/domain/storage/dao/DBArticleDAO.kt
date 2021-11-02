package co.deshbidesh.db_android.db_news_feature.news.domain.storage.dao


import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.NewsItemDB


@Dao
interface DBArticleDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(article: NewsItemDB): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(articles: List<NewsItemDB>)

    @Delete
    fun delete(article: NewsItemDB)

    @Query("DELETE FROM articles")
    fun deleteAll()
}