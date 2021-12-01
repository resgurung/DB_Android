package co.deshbidesh.db_android.db_note_feature.repository


import androidx.sqlite.db.SimpleSQLiteQuery
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteDateRepresentable
import co.deshbidesh.db_android.shared.utility.DBNoteConstant
import java.lang.StringBuilder


class DBNoteRepository(private val db: DBDatabase) {

    private val table = "db_notes"
    private val dateFormat = "unixepoch"

    fun getNoteList(index: Int): List<DBNote> {

        val offset = (index - 1) * DBNoteConstant.DB_NOTE_PAGE_SIZE

        return db.noteDAO().getNoteList(
            limit = DBNoteConstant.DB_NOTE_PAGE_SIZE,
            offset = offset)
    }

    fun getNoteListByUpdatedDate(index: Int): List<DBNote> {

        val offset = (index - 1) * DBNoteConstant.DB_NOTE_PAGE_SIZE

        return db.noteDAO().getNotesByUpdatedDate(
            limit = DBNoteConstant.DB_NOTE_PAGE_SIZE,
            offset = offset)
    }

    fun search(query: String, index: Int): List<DBNote> {

        val offset = (index - 1) * DBNoteConstant.DB_NOTE_PAGE_SIZE

        return db.noteDAO().searchTitleWith(
            searchString = query,
            limit = DBNoteConstant.DB_NOTE_PAGE_SIZE,
            offset = offset)
    }

    // search column is either createdDate or updatedDate
    fun groupByYear(search: String): List<DBNoteDateRepresentable> {

        val finalQuery = StringBuilder()

        val query = "SELECT strftime('%Y', DATE(ROUND($search / 1000), '$dateFormat')) AS dateInt, " +
                "COUNT(*) AS count FROM $table GROUP BY dateInt ORDER BY dateInt DESC"

        finalQuery.append(query)
        val simpleSQLiteQuery = SimpleSQLiteQuery(finalQuery.toString())

        return db.noteDAO().getNotesCountByYear(simpleSQLiteQuery)
    }

    fun groupByMonth(column: String, year: Int): List<DBNoteDateRepresentable> {

        val finalQuery = StringBuilder()

        val query = "SELECT strftime('%m', DATE(ROUND($column / 1000), '$dateFormat')) AS dateInt, " +
                "COUNT(*) AS count FROM $table " +
                "WHERE strftime('%Y', DATE(ROUND($column / 1000), '$dateFormat'))='$year' GROUP BY dateInt"

        finalQuery.append(query)

        val simpleSQLiteQuery = SimpleSQLiteQuery(finalQuery.toString())

        return db.noteDAO().getNotesCountByMonth(simpleSQLiteQuery)
    }

    fun getMonthNotes(
        column: String,
        year: Int,
        month: Int
    ): List<DBNote> {
        val finalQuery = StringBuilder()
        // when single digit add 0 at the front but not for double digit
        val newMonth = if (month < 10){ "0$month" } else { "$month" }
        val query = "SELECT * FROM $table " +
                "WHERE strftime('%Y', DATE(ROUND($column / 1000), '$dateFormat'))='$year'" +
                " AND strftime('%m', DATE(ROUND($column / 1000), '$dateFormat'))='$newMonth'"

        finalQuery.append(query)

        val simpleSQLiteQuery = SimpleSQLiteQuery(finalQuery.toString())

        return db.noteDAO().getNotesByMonth(simpleSQLiteQuery)
    }

    fun singleNote(noteId: Long): DBNote {

        return db.noteDAO().singleNote(noteId)
    }

    fun addNote(note: DBNote): Long {

        return db.noteDAO().addNote(note)
    }

    fun updateNote(note: DBNote) {

        db.noteDAO().updateNote(note)
    }

    fun delete(note: DBNote) {

        db.noteDAO().deleteNote(note)
    }

    fun deleteAll() {

        db.noteDAO().deleteAll()
    }

    suspend fun getImageIdListByNoteId(id: Int): String? {

        return db.noteDAO().getImageIdListByNoteId(id)
    }
}