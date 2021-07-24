package co.deshbidesh.db_android.db_news_feature.news.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_network.domain.DBNewsArticleMediator
import co.deshbidesh.db_android.db_network.domain.DBNewsRepository
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsUiModel
import co.deshbidesh.db_android.db_news_feature.news.storage.relations.ArticleWithCategories
import co.deshbidesh.db_android.shared.utility.DBNewsConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class DBNewsArticleViewModel(): ViewModel() {

    var repository: DBNewsRepository? = null

    @ExperimentalPagingApi
    val getPagedList: Flow<PagingData<ArticleWithCategories>>? = repository
        ?.getArticles()
        ?.cachedIn(viewModelScope)


    @ExperimentalPagingApi
    fun getRecentArticleWithCategory(callback: (List<ArticleWithCategories>) -> Unit ) {

        repository?.let { repos ->

            viewModelScope.launch(Dispatchers.IO) {

                val itemList: MutableList<ArticleWithCategories> =
                    repos.database.articleCategoryJoinDAO().getHomeArticlesWithCategories()

                if (itemList.isEmpty()) {

                    val response = repos.service?.getArticles(DBNewsConstants.PAGE_SIZE,1)

                    if (response?.isSuccessful == true) {

                        response.body()?.let { news ->

                            DBNewsArticleMediator.insertToDB(repos.database, news, 0, 2)


                            val itemList: MutableList<ArticleWithCategories> =
                                repos.database.articleCategoryJoinDAO().getHomeArticlesWithCategories()

                            callback(itemList)
                        } ?: run {

                            callback(ArrayList())
                        }
                    }

                } else {

                    callback(itemList)
                }

            }
        }

    }

}