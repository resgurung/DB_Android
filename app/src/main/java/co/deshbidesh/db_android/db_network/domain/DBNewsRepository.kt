package co.deshbidesh.db_android.db_network.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_network.db_retrofit.DBRetrofitInstance
import co.deshbidesh.db_android.db_network.db_services.DBNewsQuery
import co.deshbidesh.db_android.db_network.db_services.DBNewsService
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.shared.utility.DBNewsConstants
import kotlinx.coroutines.flow.Flow

class DBNewsRepository(
    val database:   DBDatabase,
    private val service:    DBNewsService? = DBRetrofitInstance.dbNewsApi,
    private val query:      DBNewsQuery? = null
) {

    @ExperimentalPagingApi
    fun getArticles(): Flow<PagingData<ArticleWithCategories>> {

        val pagingSourceFactory = {  database.articleCategoryJoinDAO().getArticlesWithCategories()  }

        return Pager(
            config = PagingConfig(
                pageSize = DBNewsConstants.PAGE_SIZE,
                maxSize = DBNewsConstants.PAGE_SIZE + ( DBNewsConstants.PAGE_SIZE * 2),
                enablePlaceholders = false
            ),
            remoteMediator = service?.let {
                DBNewsArticleMediator(
                    it,
                    database,
                    query
                )
            },
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}