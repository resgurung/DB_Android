package co.deshbidesh.db_android.db_note_feature.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteListViewModel

class DBNoteListViewModelFactory(
    private val repository: DBNoteRepository
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DBNoteListViewModel::class.java)) {

            return DBNoteListViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}