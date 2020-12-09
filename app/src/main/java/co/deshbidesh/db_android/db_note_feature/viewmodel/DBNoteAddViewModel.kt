package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteLanguage
import co.deshbidesh.db_android.shared.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DBNoteAddViewModel (
    private val repository: DBNoteRepository,
    private val utility: DBHelper
): ViewModel() {

    lateinit var title: String

    lateinit var content: String

    fun addNote() {

        val note = DBNote(
            0,
            title,
            utility.generateDescriptionFromContent(content),
            content,
            Date(),
            Date(),
            DBNoteLanguage.ENGLISH
        )

        viewModelScope.launch(Dispatchers.IO) {

            repository.addNote(note)
        }
    }

    fun isTextEmpty(): Boolean {

        if (title.isEmpty() || content.isEmpty()) {

            return true
        }

        return false
    }
}