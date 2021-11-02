package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_note_feature.repository.DBImageRepository
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBImage
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteLanguage
import co.deshbidesh.db_android.shared.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class DBNoteAddViewModel(
    private val noteRepository: DBNoteRepository,
    private val imageRepository: DBImageRepository
): ViewModel() {

    lateinit var title: String

    lateinit var content: String

    fun addNote(listener: (Long?) -> Unit) {

        val note = DBNote(
            0,
            title,
            DBHelper.generateDescriptionFromContent(content),
            content,
            null,
            Date(),
            Date(),
            DBNoteLanguage.ENGLISH
        )

        viewModelScope.launch(Dispatchers.IO) {

            val noteId = noteRepository.addNote(note)

            listener(noteId)
        }
    }

    fun getNote(id: Long, listener: (DBNote) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            listener(noteRepository.singleNote(id))
        }
    }

    fun updateNote(note: DBNote, listener: () -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            noteRepository.updateNote(note)

            listener()
        }
    }

    fun addImage(paths: List<String>, noteId: Long?, listener: (list: ArrayList<String>) -> Unit) {

        var imageIdList: ArrayList<String> = arrayListOf()

        viewModelScope.launch (Dispatchers.IO){

            if (paths.isNotEmpty()) {

                for (path in paths) {

                    val dbImage = DBImage(
                        0,
                        path,
                        noteId?.toInt()
                    )

                    val id = imageRepository.addImage(dbImage)

                    imageIdList.add(id.toString())
                }
            }

            listener(imageIdList)
        }
    }


    fun isTextEmpty(): Boolean {

        if (title.isEmpty() || content.isEmpty()) {

            return true
        }

        return false
    }
}