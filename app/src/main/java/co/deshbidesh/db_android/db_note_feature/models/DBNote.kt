package co.deshbidesh.db_android.db_note_feature.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.deshbidesh.db_android.db_database.database.database_converter.DBNoteLanguageConverter
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "db_notes")
@TypeConverters(DBNoteLanguageConverter::class)

data class DBNote (

    @PrimaryKey(autoGenerate = true)
    val id:             Int,

    var title:          String,

    var description:    String,

    var content:        String,

    var imageIds:       ArrayList<String>?,

    val createdDate:    Date?,

    var updatedDate:    Date?,

    val languageType:   DBNoteLanguage

): Parcelable



/*
   Note delete all image sud be deleted
   one image delete than the note associates with it sud only put null on the imageids
 */

/*

    Note1 - image45
         - image43
         - image67

    Note2 - null

    Note3 - image1

    delete image1

    Note3 - null

    delete Note1

    all delete -> Note1 deletes -> Cascade All images delete


    //////////////////////
    Note1 -> Image1

    Note2 -> Image2

    Note3 -> null


    Manager table ->                                     address table
    (p)manager_id Name surnane (f)address_id || (p)address_id street postcode (f)manager_id
    1 rupesh mall 2                           || 2 london gt56 9up
                                              || 3 north london gf45 67
    manager delete cascade  deltes address_id


    Pivot table
    Manager table id || addressTable id
    1                ||     2
    1                ||     3

 */