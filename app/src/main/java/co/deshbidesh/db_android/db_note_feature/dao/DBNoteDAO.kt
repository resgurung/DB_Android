package co.deshbidesh.db_android.db_note_feature.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteDateRepresentable
import java.util.*
import kotlin.collections.ArrayList

@Dao
interface DBNoteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: DBNote): Long

    @Update
    fun updateNote(note: DBNote)

    @Delete
    fun deleteNote(note: DBNote)

    @Query("DELETE from db_notes")
    fun deleteAll()

    @Query("SELECT * FROM db_notes WHERE id= :id" )
    fun singleNote(id: Long): DBNote

    @Query("SELECT * FROM db_notes ORDER BY createdDate DESC")
    fun getPagedNotes(): PagingSource<Int, DBNote>

    @Query("SELECT * FROM db_notes ORDER BY createdDate DESC")
    fun readAllNotesDESC(): LiveData<List<DBNote>>

    @Query("SELECT * FROM db_notes ORDER BY createdDate ASC")
    fun readAllNotesASC(): LiveData<List<DBNote>>

    @Query("SELECT * FROM db_notes WHERE createdDate  BETWEEN :from AND :to ")
    fun findAllNotesBetween(from: Date, to: Date): LiveData<List<DBNote>>

    @Query("SELECT * FROM db_notes WHERE title LIKE :searchString")
    fun searchTitle(searchString: String): LiveData<List<DBNote>>

    @Query("SELECT imageIds FROM db_notes WHERE id= :id")
    fun getImageIdListByNoteId(id: Int): String?

    //
    @Query("SELECT * FROM db_notes WHERE title LIKE '%' || :searchString || '%' LIMIT :limit OFFSET :offset")
    fun searchTitleWith(searchString: String, limit: Int, offset: Int): List<DBNote>

    @Query("SELECT * FROM db_notes ORDER BY createdDate DESC LIMIT :limit OFFSET :offset")
    fun getNoteList(limit: Int, offset: Int): List<DBNote>

    @Query("SELECT * FROM db_notes ORDER BY updatedDate DESC LIMIT :limit OFFSET :offset")
    fun getNotesByUpdatedDate(limit: Int, offset: Int): List<DBNote>

    @Query("SELECT * FROM db_notes ORDER BY createdDate DESC")
    fun getNotes(): List<DBNote>

    @RawQuery(observedEntities = [DBNote::class])
    fun getNotesCountByYear(query: SupportSQLiteQuery?): List<DBNoteDateRepresentable>

    @RawQuery(observedEntities = [DBNote::class])
    fun getNotesCountByMonth(query: SupportSQLiteQuery?): List<DBNoteDateRepresentable>

    @RawQuery(observedEntities = [DBNote::class])
    fun getNotesByMonth(query: SupportSQLiteQuery?): List<DBNote>

    //
}