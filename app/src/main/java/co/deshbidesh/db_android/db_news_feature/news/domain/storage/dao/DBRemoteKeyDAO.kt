package co.deshbidesh.db_android.db_news_feature.news.domain.storage.dao

import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.NewsRemoteKey


@Dao
interface DBRemoteKeyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRemoteKey(keys: List<NewsRemoteKey>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKey(remoteKey: NewsRemoteKey): Long

    @Query("SELECT * FROM news_remote_key WHERE news_remote_key.r_id = :remoteKeyId")
    fun getRemoteKey(remoteKeyId: Long): NewsRemoteKey

    @Query("SELECT * FROM news_remote_key")
    fun getAllRemoteKey(): List<NewsRemoteKey>

    @Delete
    fun delete(key: NewsRemoteKey)

    @Query("DELETE FROM news_remote_key")
    fun deleteAll()
}