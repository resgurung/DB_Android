package co.deshbidesh.db_android.db_note_feature.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel

class DBNoteDetailViewModelFactory(
    private val repository: DBNoteRepository,
    private val note: DBNote
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DBNoteDetailViewModel::class.java)) {

            return DBNoteDetailViewModel(repository, note) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}