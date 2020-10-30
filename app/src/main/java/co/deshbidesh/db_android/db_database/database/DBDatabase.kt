package co.deshbidesh.db_android.db_database.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.deshbidesh.db_android.db_database.dao.DBNoteDAO
import co.deshbidesh.db_android.db_database.database.DatabaseConverter.DBDateConverter
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.R

@Database(entities = [DBNote::class], version = 1)
@TypeConverters(DBDateConverter::class)
abstract class DBDatabase: RoomDatabase() {

    abstract fun noteDAO(): DBNoteDAO

    companion object {

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