package co.deshbidesh.db_android.db_note_feature.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_database.repository.DBImageRepository
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBImage
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.shared.DBHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBNoteDetailViewModel(
    private val noteRepository: DBNoteRepository,
    private val imageRepository: DBImageRepository,
    private val utility: DBHelper,
    private val note: DBNote
): ViewModel() {

    lateinit var title: String

    lateinit var content: String

    var updatedImageIds: ArrayList<String> = arrayListOf()


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



    fun getImageIdByPath(imagePath: String, listener: (id: Int) -> Unit){

        viewModelScope.launch {
            val imgId: Int = imageRepository.getImageIdByPath(imagePath)

            listener(imgId)
        }
    }


    fun deleteSingleImageById(imgId: Int, listener: () -> Unit){

        viewModelScope.launch {
            imageRepository.deleteSingleImageById(imgId)

            listener()
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

    fun updateNote() {

        note.title = title

        note.content = content

        note.description = utility.generateDescriptionFromContent(content)

        if(updatedImageIds.isNotEmpty()){

            note.imageIds = updatedImageIds
        }

        viewModelScope.launch(Dispatchers.IO) {

            noteRepository.updateNote(note)
        }
    }

    fun addImage(paths: List<String>, listener: (list: ArrayList<String>) -> Unit) {

        var previousImageList: ArrayList<String>? = note.imageIds

        var imageIdList: ArrayList<String> = arrayListOf()

        viewModelScope.launch (Dispatchers.IO){

            if (paths.isNotEmpty()) {

                for (path in paths) {

                    val dbImage = DBImage(
                        0,
                        path,
                        note.id
                    )

                    val id = imageRepository.addImage(dbImage)

                    imageIdList.add(id.toString())
                }

                previousImageList?.addAll(imageIdList)
            }

            listener(previousImageList!!)
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

}