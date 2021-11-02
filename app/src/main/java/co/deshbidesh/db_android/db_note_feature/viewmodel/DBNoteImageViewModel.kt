package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_note_feature.repository.DBImageRepository
import co.deshbidesh.db_android.db_note_feature.models.DBImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DBNoteImageViewModel(

    val repository: DBImageRepository

): ViewModel() {

    lateinit var imagePath: String

    var noteId: Int? = null

    fun addImage(){

        val image = DBImage(
            0,
            imagePath,
            noteId
        )

        viewModelScope.launch(Dispatchers.IO) {

            repository.addImage(image)
        }
    }
}