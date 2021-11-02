package co.deshbidesh.db_android.db_note_feature.repository

import co.deshbidesh.db_android.db_note_feature.dao.DBImageDAO
import co.deshbidesh.db_android.db_note_feature.models.DBImage

class DBImageRepository(private val noteImageDao: DBImageDAO) {

    fun addImage(noteImage: DBImage): Long {
        return noteImageDao.addImage(noteImage)
    }

    fun getSingleImageByImageId(imageId: Int): DBImage {
        return noteImageDao.getSingleImageByImageId(imageId)
    }

    fun getImageListByNoteId(noteId: Int): List<DBImage> {
        return noteImageDao.getImageListByNoteId(noteId)
    }

    fun getImageIdByPath(imagePath: String): Int{
        return noteImageDao.getImageIdByPath(imagePath)
    }

    fun deleteSingleImageById(imgId: Int) {
        noteImageDao.deleteSingleImageById(imgId)
    }

    fun deleteImagesByNoteId(noteId: Int){
        noteImageDao.deleteImageByNoteId(noteId)
    }
}