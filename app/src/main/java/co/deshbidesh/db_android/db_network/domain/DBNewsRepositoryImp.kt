package co.deshbidesh.db_android.db_network.domain

import androidx.paging.*
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_network.db_retrofit.DBRetrofitInstance
import co.deshbidesh.db_android.db_network.db_services.DBNewsQuery
import co.deshbidesh.db_android.db_network.db_services.DBNewsService
import co.deshbidesh.db_android.db_news_feature.news.domain.repositories.DBNewsRepository
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.shared.utility.DBNewsConstants
import kotlinx.coroutines.flow.Flow

class DBNewsRepositoryImp(
    val database:   DBDatabase,
    private val service:    DBNewsService = DBRetrofitInstance.dbNewsApi,
    private val query:      DBNewsQuery? = null
): DBNewsRepository {

    @ExperimentalPagingApi
    override fun getArticles(): Flow<PagingData<ArticleWithCategories>> {

        val pagingSourceFactory = {
            database.articleCategoryJoinDAO().getArticlesWithCategories()
        }

        val config = PagingConfig(
            pageSize = DBNewsConstants.PAGE_SIZE,
            initialLoadSize = DBNewsConstants.PAGE_SIZE,
            maxSize = DBNewsConstants.PAGE_SIZE + (DBNewsConstants.PAGE_SIZE * 2),
            enablePlaceholders = false
        )

        val remoteMediator = DBNewsArticleMediator(
            service,
            database,
            query
        )

        return Pager(
            config = config,
            remoteMediator = remoteMediator,
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }
}

