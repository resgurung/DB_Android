package co.deshbidesh.db_android.db_database.repository

import co.deshbidesh.db_android.db_database.dao.DBImageDAO
import co.deshbidesh.db_android.db_note_feature.models.DBImage

class DBImageRepository(private val noteImageDao: DBImageDAO) {

    suspend fun addImage(noteImage: DBImage): Long {
        return noteImageDao.addImage(noteImage)
    }

    suspend fun getSingleImageByImageId(imageId: Int): DBImage {
        return noteImageDao.getSingleImageByImageId(imageId)
    }

    suspend fun getImageListByNoteId(noteId: Int): List<DBImage> {
        return noteImageDao.getImageListByNoteId(noteId)
    }

    suspend fun deleteImagesByNoteId(noteId: Int){
        noteImageDao.deleteImageByNoteId(noteId)
    }
}