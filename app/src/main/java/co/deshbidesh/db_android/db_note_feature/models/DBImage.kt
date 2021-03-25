package co.deshbidesh.db_android.db_note_feature.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(tableName = "db_images")
data class DBImage(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id:                             Int,

    @ColumnInfo(defaultValue = " ", name = "img_path")
    val imgPath:                        String,

    @ColumnInfo(name = "note_id")
    var noteId:                         Int?

)