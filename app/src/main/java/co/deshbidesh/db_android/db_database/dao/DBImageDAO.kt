package co.deshbidesh.db_android.db_database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import co.deshbidesh.db_android.db_note_feature.models.DBImage

@Dao
interface DBImageDAO {

    /* @Query("SELECT * FROM note_image_table INNER JOIN user_table ON note_image_table.user_id = user_table.id WHERE note_image_table.user_id = :userId")
   suspend fun getNoteImages(userId: Int): LiveData<List<NoteImage>>*/


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImage(image: DBImage): Long

}