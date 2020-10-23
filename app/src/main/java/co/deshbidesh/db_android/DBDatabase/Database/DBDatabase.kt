package co.deshbidesh.db_android.DBDatabase.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.deshbidesh.db_android.DBDatabase.DAO.DBNoteDAO
import co.deshbidesh.db_android.DBDatabase.Database.DatabaseConverter.DBDateConverter
import co.deshbidesh.db_android.DBNoteFeature.Model.DBNote
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