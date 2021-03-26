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
import java.io.File

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

    fun getImageIdListByNoteId(listener: (idList: String?) -> Unit){

        var imageIdList: String?
        viewModelScope.launch {
             imageIdList = noteRepository.getImageIdListByNoteId(note.id)

             listener(imageIdList)
        }
    }


    fun getImageIdByPath(imagePathList: ArrayList<String> , listener: (ids: ArrayList<String> ) -> Unit){

        viewModelScope.launch {

            val imgIds: ArrayList<String> = arrayListOf()

            for(path in imagePathList){

                val id = imageRepository.getImageIdByPath(path)

                imgIds.add(id.toString())
            }

            listener(imgIds)
        }
    }


    fun deleteSingleImageById(imgIds: ArrayList<String>, listener: () -> Unit){

        viewModelScope.launch {

            for (id in imgIds){

                imageRepository.deleteSingleImageById(id.toInt())

            }

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

    fun updateNote(listener: () -> Unit) {

        note.title = title

        note.content = content

        note.description = utility.generateDescriptionFromContent(content)

        println("imgUpdated2 $updatedImageIds")

        if(updatedImageIds.isNotEmpty()){

            note.imageIds = updatedImageIds
        }else{
            note.imageIds = null
        }

        viewModelScope.launch(Dispatchers.IO) {

            noteRepository.updateNote(note)
            listener()
        }
    }

    fun addImage(paths: List<String>, listener: (list: ArrayList<String>) -> Unit) {

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
            }

            listener(imageIdList)
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