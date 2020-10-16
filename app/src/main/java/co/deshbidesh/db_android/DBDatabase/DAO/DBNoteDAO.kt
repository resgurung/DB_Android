package co.deshbidesh.db_android.DBDatabase.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import co.deshbidesh.db_android.DBNoteFeature.Model.DBNote
import java.util.*

@Dao
interface DBNoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: DBNote)

    @Update
    suspend fun updateNote(note: DBNote)

    @Delete
    suspend fun deleteNote(note: DBNote)

    @Query("DELETE from db_notes")
    suspend fun deleteAll()

    @Query("SELECT * FROM db_notes ORDER BY createdDate DESC")
    fun readAllNotesDESC(): LiveData<List<DBNote>>

    @Query("SELECT * FROM db_notes ORDER BY createdDate ASC")
    fun readAllNotesASC(): LiveData<List<DBNote>>

    @Query("SELECT * FROM db_notes WHERE createdDate  BETWEEN :from AND :to ")
    fun findAllNotesBetween(from: Date, to: Date): LiveData<List<DBNote>>

    @Query("SELECT * FROM db_notes WHERE content= :searchString")
    fun searchContent(searchString: String): LiveData<List<DBNote>>
}