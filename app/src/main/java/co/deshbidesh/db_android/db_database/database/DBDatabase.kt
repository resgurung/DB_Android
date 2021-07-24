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
import co.deshbidesh.db_android.db_news_feature.news.storage.dao.DBArticleCategoryDAO
import co.deshbidesh.db_android.db_news_feature.news.storage.dao.DBArticleDAO
import co.deshbidesh.db_android.db_news_feature.news.storage.dao.DBCategoryDAO
import co.deshbidesh.db_android.db_news_feature.news.storage.dao.DBRemoteKeyDAO
import co.deshbidesh.db_android.db_news_feature.news.storage.entity.CategoryDB
import co.deshbidesh.db_android.db_news_feature.news.storage.entity.NewsItemDB
import co.deshbidesh.db_android.db_news_feature.news.storage.entity.NewsRemoteKey
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleCategoryJoin
import co.deshbidesh.db_android.db_note_feature.models.DBImage

@Database(entities = [
                        DBNote::class,
                        DBImage::class,
                        NewsItemDB::class,
                        CategoryDB::class,
                        ArticleCategoryJoin::class,
                        NewsRemoteKey::class
                     ], version = 2)
@TypeConverters(
    DBDateConverter::class,
    DBImageIdListConverter::class
)
abstract class DBDatabase: RoomDatabase() {

    // notes dao
    abstract fun noteDAO(): DBNoteDAO

    abstract fun imageDAO(): DBImageDAO

    // news dao
    abstract fun articleDAO(): DBArticleDAO

    abstract fun categoryDAO(): DBCategoryDAO

    abstract fun remoteKeyDAO(): DBRemoteKeyDAO

    abstract fun articleCategoryJoinDAO(): DBArticleCategoryDAO

    companion object {

        @Volatile
        private var INSTANCE: DBDatabase? = null

        fun getDatabase(context: Context): DBDatabase {

            val tempINSTANCE = INSTANCE

            if (tempINSTANCE != null) {

                return tempINSTANCE
            }


            synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DBDatabase::class.java,
                    context.getString(R.string.DatabaseName)
                ).build()

                INSTANCE = instance

                return instance
            }
        }
    }
}