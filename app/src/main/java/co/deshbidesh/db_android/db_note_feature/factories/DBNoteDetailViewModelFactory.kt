package co.deshbidesh.db_android.db_note_feature.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_note_feature.repository.DBImageRepository
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBHelper

object DBNoteDetailViewModelFactory: ViewModelProvider.Factory {

    private lateinit var noteRepository: DBNoteRepository

    private lateinit var imageRepository: DBImageRepository

    fun inject(noteRepository: DBNoteRepository, imageRepository: DBImageRepository) {
        DBNoteDetailViewModelFactory.noteRepository = noteRepository
        DBNoteDetailViewModelFactory.imageRepository = imageRepository
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DBNoteDetailViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")
            return DBNoteDetailViewModel(noteRepository, imageRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}