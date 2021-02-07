package co.deshbidesh.db_android.db_database.repository

import co.deshbidesh.db_android.db_database.dao.DBImageDAO
import co.deshbidesh.db_android.db_note_feature.models.DBImage

class DBImageRepository(private val noteImageDao: DBImageDAO) {

    /*suspend fun getNoteImages(userId: Int): LiveData<List<NoteImage>> {
       return noteImageDao.getNoteImages(userId)
   }*/

    // Method runs in separate thread
    suspend fun addImage(noteImage: DBImage): Long {
        return noteImageDao.addImage(noteImage)
    }
}