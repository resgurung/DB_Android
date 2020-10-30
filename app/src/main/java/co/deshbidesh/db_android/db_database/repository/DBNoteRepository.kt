package co.deshbidesh.db_android.db_database.repository

import androidx.lifecycle.LiveData
import co.deshbidesh.db_android.db_database.dao.DBNoteDAO
import co.deshbidesh.db_android.db_note_feature.models.DBNote

class DBNoteRepository(private val dao: DBNoteDAO) {

    val readAllNotes: LiveData<List<DBNote>> = dao.readAllNotesDESC()

    suspend fun addNote(note: DBNote) {

        dao.addNote(note)
    }

    suspend fun updateNote(note: DBNote) {

        dao.updateNote(note)
    }

    suspend fun delete(note: DBNote) {

        dao.deleteNote(note)
    }

    suspend fun deleteAll() {

        dao.deleteAll()
    }
}