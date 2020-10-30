package co.deshbidesh.db_android.db_note_feature.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBNoteViewModel(application: Application): AndroidViewModel(application) {

    val readAllNotes: LiveData<List<DBNote>>

    private val repository: DBNoteRepository

    init {

        val dao = DBDatabase.getDatabase(application).noteDAO()

        repository = DBNoteRepository(dao)

        readAllNotes = repository.readAllNotes
    }

    fun addNote(note: DBNote) {

        viewModelScope.launch(Dispatchers.IO) {

            repository.addNote(note)
        }
    }

    fun updateNote(note: DBNote) {

        viewModelScope.launch {

            repository.updateNote(note)
        }
    }

    fun deleteNote(note: DBNote) {

        viewModelScope.launch {

            repository.delete(note)
        }
    }

    fun deleteAll() {

        viewModelScope.launch {

            repository.deleteAll()
        }
    }
}