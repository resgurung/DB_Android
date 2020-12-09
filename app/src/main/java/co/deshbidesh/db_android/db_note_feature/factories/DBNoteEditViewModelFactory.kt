package co.deshbidesh.db_android.db_note_feature.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteEditViewModel
import co.deshbidesh.db_android.shared.DBHelper

class DBNoteEditViewModelFactory(
    private val repository: DBNoteRepository,
    private val note: DBNote,
    private val utility: DBHelper
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DBNoteEditViewModel::class.java)) {

            return DBNoteEditViewModel(repository, note, utility) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}