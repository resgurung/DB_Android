package co.deshbidesh.db_android.db_news_feature.news.domain.storage.dao

import androidx.room.*
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.CategoryDB

@Dao
interface DBCategoryDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdate(category: CategoryDB): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<CategoryDB>)

    @Query("SELECT * FROM categories")
    fun getAllCategory(): List<CategoryDB>

    @Query("SELECT * FROM categories WHERE categories.c_id = :categoryId")
    fun getCategory(categoryId: Long): CategoryDB

    @Delete
    fun delete(category: CategoryDB)

    @Query("DELETE FROM categories")
    fun deleteAll()

}