package co.deshbidesh.db_android.db_home_feature.domain

import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import kotlinx.coroutines.flow.Flow

class DBHomeRepository(database: DBDatabase) {
    var allFeaturedArticles: Flow<List<ArticleWithCategories>> = database
        .articleCategoryJoinDAO()
        .getHomeArticlesWithCategories(true)

}