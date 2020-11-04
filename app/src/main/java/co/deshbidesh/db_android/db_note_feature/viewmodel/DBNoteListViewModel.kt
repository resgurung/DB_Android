package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository

class DBNoteListViewModel(
    private val repository: DBNoteRepository
): ViewModel() {

    val getPagedList = Pager(
        PagingConfig(
            pageSize = 14,
            enablePlaceholders = false,
            maxSize = 50
        )
    ){
        repository.readAllNotes

    }.flow.cachedIn(viewModelScope)
}