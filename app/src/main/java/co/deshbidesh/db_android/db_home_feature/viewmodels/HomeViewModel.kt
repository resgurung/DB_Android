package co.deshbidesh.db_android.db_home_feature.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import co.deshbidesh.db_android.db_home_feature.domain.DBHomeRepository
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories

class HomeViewModel(repository: DBHomeRepository): ViewModel() {

    var allFeaturedArticles: LiveData<List<ArticleWithCategories>> = repository
        .allFeaturedArticles
        .asLiveData()
}