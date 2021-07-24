package co.deshbidesh.db_android.db_news_feature.news.storage.dao

import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.storage.entity.CategoryDB

@Dao
interface DBCategoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(category: CategoryDB): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryDB>)

    @Query("SELECT * FROM categories")
    suspend fun getAllCategory(): List<CategoryDB>

    @Query("SELECT * FROM categories WHERE categories.c_id = :categoryId")
    suspend fun getCategory(categoryId: Long): CategoryDB

    @Delete
    suspend fun delete(category: CategoryDB)

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

}