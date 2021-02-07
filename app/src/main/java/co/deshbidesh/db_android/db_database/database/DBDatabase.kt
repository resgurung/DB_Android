package co.deshbidesh.db_android.db_database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.deshbidesh.db_android.db_database.dao.DBNoteDAO
import co.deshbidesh.db_android.db_database.database.database_converter.DBDateConverter
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_database.dao.DBImageDAO
import co.deshbidesh.db_android.db_database.database.database_converter.DBImageIdListConverter
import co.deshbidesh.db_android.db_note_feature.models.DBImage

@Database(entities = [DBNote::class, DBImage::class], version = 1)
@TypeConverters(DBDateConverter::class, DBImageIdListConverter::class)
abstract class DBDatabase: RoomDatabase() {

    abstract fun noteDAO(): DBNoteDAO

    abstract fun imageDAO(): DBImageDAO

    companion object { // singleton in java

        @Volatile
        private var INSTANCE: DBDatabase? = null

        fun getDatabase(context: Context): DBDatabase {

            val tempINSTANCE = INSTANCE

            if (tempINSTANCE != null) {

                return tempINSTANCE
            }

            synchronized(this) {

                var instance = Room.databaseBuilder(
                    context.applicationContext,
                    DBDatabase::class.java,
                    context.getString(R.string.DatabaseName)
                ).build()

                INSTANCE = instance

                return  instance
            }
        }
    }
}