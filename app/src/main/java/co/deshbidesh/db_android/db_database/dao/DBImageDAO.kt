package co.deshbidesh.db_android.db_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.deshbidesh.db_android.db_note_feature.models.DBImage
import kotlinx.coroutines.selects.select

@Dao
interface DBImageDAO {

    /* @Query("SELECT * FROM note_image_table INNER JOIN user_table ON note_image_table.user_id = user_table.id WHERE note_image_table.user_id = :userId")
   suspend fun getNoteImages(userId: Int): LiveData<List<NoteImage>>*/


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: DBImage): Long

    @Query("SELECT * FROM db_images WHERE id= :imageId")
    suspend fun getSingleImageByImageId(imageId: Int): DBImage

    @Query("SELECT * FROM db_images WHERE note_id= :noteId")
    suspend fun getImageListByNoteId(noteId: Int): List<DBImage>

    @Query("SELECT id FROM db_images WHERE img_path= :imagePath")
    suspend fun getImageIdByPath(imagePath: String): Int

    @Query("DELETE FROM db_images WHERE id= :imgId")
    suspend fun deleteSingleImageById(imgId: Int)

    @Query("DELETE FROM db_images WHERE note_id= :noteId")
    suspend fun deleteImageByNoteId(noteId: Int)

}