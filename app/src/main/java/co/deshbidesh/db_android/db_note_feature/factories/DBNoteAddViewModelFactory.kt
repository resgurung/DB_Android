package co.deshbidesh.db_android.db_note_feature.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_database.repository.DBImageRepository
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteAddViewModel
import co.deshbidesh.db_android.shared.DBHelper

class DBNoteAddViewModelFactory (
    private val noteRepository: DBNoteRepository,
    private val imageRepository: DBImageRepository,
    private val utility: DBHelper
): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DBNoteAddViewModel::class.java)) {

            return DBNoteAddViewModel(noteRepository, imageRepository, utility) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}