package co.deshbidesh.db_android.db_news_feature.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.deshbidesh.db_android.db_network.domain.DBNewsRepository
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleWithCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DBNewsArticleViewModel(
    private val repository: DBNewsRepository
): ViewModel() {

    @ExperimentalPagingApi
    val getPagedList: Flow<PagingData<ArticleWithCategories>> = repository
        .getArticles()
        .cachedIn(viewModelScope)

    @ExperimentalPagingApi
    fun getArticles() {
        repository.getArticles()
    }

    fun getRecentArticleWithCategory(list: (List<ArticleWithCategories>) -> Unit ) {

        viewModelScope.launch(Dispatchers.IO) {

            list(repository.database.articleCategoryJoinDAO().getHomeArticlesWithCategories())
        }
    }

}