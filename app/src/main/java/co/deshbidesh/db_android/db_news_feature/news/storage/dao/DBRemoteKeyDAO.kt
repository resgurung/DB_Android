package co.deshbidesh.db_android.db_news_feature.news.storage.dao

import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.storage.entity.NewsRemoteKey


@Dao
interface DBRemoteKeyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKey(keys: List<NewsRemoteKey>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKey(remoteKey: NewsRemoteKey): Long

    @Query("SELECT * FROM news_remote_key WHERE news_remote_key.r_id = :remoteKeyId")
    suspend fun getRemoteKey(remoteKeyId: Long): NewsRemoteKey

    @Query("SELECT * FROM news_remote_key")
    suspend fun getAllRemoteKey(): List<NewsRemoteKey>

    @Delete
    suspend fun delete(key: NewsRemoteKey)

    @Query("DELETE FROM news_remote_key")
    suspend fun deleteAll()
}