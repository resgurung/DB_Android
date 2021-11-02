package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.shared.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBNoteEditViewModel(
    private val repository: DBNoteRepository,
    private val note: DBNote
): ViewModel() {

    lateinit var title: String

    lateinit var content: String

    fun updateNote() {

        note.title = title

        note.content = content

        note.description = DBHelper.generateDescriptionFromContent(content)

        viewModelScope.launch(Dispatchers.IO) {

            repository.updateNote(note)
        }
    }

    fun isSame(): Boolean {

        return this.note.content == content && this.note.title == title
    }

    fun isTextEmpty(): Boolean {

        if (title.isEmpty() || content.isEmpty()) {

            return true
        }

        return false
    }

    fun getNote(): DBNote {

        return this.note
    }
}