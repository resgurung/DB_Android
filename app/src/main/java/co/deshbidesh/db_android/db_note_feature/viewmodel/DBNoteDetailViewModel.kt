package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import kotlinx.coroutines.launch

class DBNoteDetailViewModel(
    private val repository: DBNoteRepository,
    private val note: DBNote
): ViewModel() {

    fun getNote(): DBNote {

        return note
    }

    fun deleteNote() {

        viewModelScope.launch {

            repository.delete(note)
        }
    }
}