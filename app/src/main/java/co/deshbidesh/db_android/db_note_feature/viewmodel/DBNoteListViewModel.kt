package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import kotlinx.coroutines.flow.Flow

class DBNoteListViewModel(
    private val repository: DBNoteRepository
): ViewModel() {

    val getPagedList: Flow<PagingData<DBNote>> = repository.getPagedNotes().cachedIn(viewModelScope)

}