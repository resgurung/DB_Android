package co.deshbidesh.db_android.db_news_feature.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_network.domain.DBNewsRepository
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteListViewModel

class DBNewsArticleViewModelFactory(
    private val repository: DBNewsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DBNewsArticleViewModel::class.java)) {

            return DBNewsArticleViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}