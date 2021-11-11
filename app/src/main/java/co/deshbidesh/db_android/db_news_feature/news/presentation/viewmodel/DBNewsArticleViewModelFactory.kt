package co.deshbidesh.db_android.db_news_feature.news.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.paging.ExperimentalPagingApi
import co.deshbidesh.db_android.db_news_feature.news.domain.repositories.DBNewsRepository

object DBNewsArticleViewModelFactory: ViewModelProvider.Factory {

    private lateinit var repository: DBNewsRepository

    fun inject(repository: DBNewsRepository) {
        DBNewsArticleViewModelFactory.repository = repository
    }
    @ExperimentalPagingApi
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return DBNewsArticleViewModel(repository) as T
    }

}