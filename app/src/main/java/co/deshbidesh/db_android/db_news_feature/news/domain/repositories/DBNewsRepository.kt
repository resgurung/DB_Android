package co.deshbidesh.db_android.db_news_feature.news.domain.repositories

import androidx.paging.PagingData
import co.deshbidesh.db_android.db_news_feature.news.domain.models.MenuNewsAdListModel
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface DBNewsRepository {
    fun getArticles(): Flow<PagingData<ArticleWithCategories>>
}