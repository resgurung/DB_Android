package co.deshbidesh.db_android.db_note_feature.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_note_feature.models.DBImage
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteLanguage
import co.deshbidesh.db_android.db_note_feature.models.DBNoteUIImage
import co.deshbidesh.db_android.db_note_feature.repository.DBImageRepository
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.utility.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class DBNoteDetailViewModel(
    private val noteRepository: DBNoteRepository,
    private val imageRepository: DBImageRepository
): ViewModel() {

    lateinit var fileUtils: FileUtils

    var currentPair: Pair<Bitmap?, File?>? = null

    private var _images: MutableLiveData<List<DBImage>> = MutableLiveData()

    val images: LiveData<List<DBImage>>
        get() = _images

    fun getNote(id: Long, listener: (DBNote) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            listener(noteRepository.singleNote(id))
        }
    }

    fun getImagesFromDatabase(id: Int) {
        viewModelScope.launch (Dispatchers.IO){
            val dbImages = imageRepository.getImageListByNoteId(id)
            _images.postValue(dbImages)
        }
    }


    fun addNote(
        title: String,
        content: String,
        listener: (Long?) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

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

            val noteId = noteRepository.addNote(note)

            listener(noteId)
        }
    }

    fun updateNote(note: DBNote, listener: (Boolean) -> Unit) {

        note.description = DBHelper.generateDescriptionFromContent(note.content)

        viewModelScope.launch(Dispatchers.IO) {

            noteRepository.updateNote(note)

            listener(true)
        }
    }

    fun addImage(noteId: Int, path: String, listener: () -> Unit) {

        if (noteId != 0) {

            viewModelScope.launch (Dispatchers.IO){

                val dbImage = DBImage(
                    0,
                    path,
                    noteId
                )

                imageRepository.addImage(dbImage)

                listener()
            }
        } else {
            listener()
        }

    }

    fun deleteImage(imgObj: DBNoteUIImage?, listener: (Boolean) -> Unit){

        imgObj?.let {

            viewModelScope.launch(Dispatchers.IO) {

                // remove the file object from the gallery
                fileUtils.deleteFile(it.imagePath)
                // remove the file row from the db
                imageRepository.deleteSingleImageById(it.id)
                // all went goo return true
                listener(true)
            }
        } ?: run {
            listener(false)
        }

    }

    fun clearCurrentPair() {
        // second is file on the cache dir, better to delete it by ourself rather
        // wait for system to clear
        if (currentPair?.second?.exists() == true) {
            currentPair?.second?.delete()
        }
        // nullify currentPair
        currentPair = null // this is bit map
    }

    fun deleteNote(note: DBNote, listener: (Boolean) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            // retrieve all images reference
            val imageList = imageRepository.getImageListByNoteId(note.id)
            // remove all image from gallery
            imageList.map { fileUtils.deleteFile(it.imgPath) }
            // delete all images reference from database
            imageRepository.deleteImagesByNoteId(note.id)
            // delete note
            noteRepository.delete(note)

            listener(true)
        }
    }

}