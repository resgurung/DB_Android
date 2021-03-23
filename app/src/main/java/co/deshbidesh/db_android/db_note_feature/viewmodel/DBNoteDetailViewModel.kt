package co.deshbidesh.db_android.db_note_feature.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_database.repository.DBImageRepository
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBImage
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import kotlinx.coroutines.launch

class DBNoteDetailViewModel(
    private val noteRepository: DBNoteRepository,
    private val imageRepository: DBImageRepository,
    private val note: DBNote
): ViewModel() {

    fun getNote(): DBNote {

        return note
    }

    fun getSingleImageByImageId(imageId: Int): DBImage?{
        var dbImage: DBImage? = null
        viewModelScope.launch {

            dbImage = imageRepository.getSingleImageByImageId(imageId)
        }
        return dbImage
    }

    fun getImageListByNoteId(listener: (imgList: List<DBImage>)->Unit) {

        val noteId: Int = note.id

        viewModelScope.launch {

            val imageList = imageRepository.getImageListByNoteId(noteId)

            listener(imageList)
        }
    }


    fun deleteNote(listener: (note: DBNote) -> Unit) {

        viewModelScope.launch {

            noteRepository.delete(note)

            listener(note)
        }
    }

    fun deleteImages(listener: () -> Unit){

        if(note.imageIds != null){

            val noteId: Int = note.id

            viewModelScope.launch {

                imageRepository.deleteImagesByNoteId(noteId)

                listener()
            }
        }
    }


}