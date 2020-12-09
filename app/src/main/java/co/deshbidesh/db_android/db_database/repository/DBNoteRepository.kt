package co.deshbidesh.db_android.db_database.repository


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import co.deshbidesh.db_android.db_database.dao.DBNoteDAO
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import kotlinx.coroutines.flow.Flow


class DBNoteRepository(private val dao: DBNoteDAO) {

    fun getPagedNotes(): Flow<PagingData<DBNote>> {

        return Pager(

            PagingConfig(
                pageSize = 14,
                enablePlaceholders = false,
                maxSize = 50
            ),
            pagingSourceFactory = { dao.getPagedNotes() }
        ).flow
    }

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