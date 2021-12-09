package co.deshbidesh.db_android.db_news_feature.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.room.Database
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_network.domain.DBNewsRepositoryImp
import co.deshbidesh.db_android.db_news_feature.news.domain.models.MenuNewsAdListModel
import co.deshbidesh.db_android.db_news_feature.news.domain.repositories.DBNewsRepository
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Lazily
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlin.coroutines.CoroutineContext

@ExperimentalPagingApi
class DBNewsArticleViewModel(val repository: DBNewsRepository) : ViewModel() {

    var homeInnerImageSliderFragmentShown: Boolean = false

    private val _latestNews: SharedFlow<PagingData<ArticleWithCategories>> = repository
        .getArticles()
        .flowOn(Dispatchers.IO)
        .cachedIn(viewModelScope)
        .shareIn(
            scope = viewModelScope,
            replay = 1,
            started = WhileSubscribed()
        )


    fun connect(): SharedFlow<PagingData<ArticleWithCategories>> = _latestNews

    fun homeData() {

    }
}