package co.deshbidesh.db_android.db_network.domain

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_network.db_services.DBNewsQuery
import co.deshbidesh.db_android.db_network.db_services.DBNewsService
import co.deshbidesh.db_android.db_news_feature.news.domain.models.DBNewsResponse
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.CategoryDB
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.NewsItemDB
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.entity.NewsRemoteKey
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleCategoryJoin
import co.deshbidesh.db_android.db_news_feature.news.domain.storage.relations.ArticleWithCategories
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@ExperimentalPagingApi
class DBNewsArticleMediator(
    private val service:        DBNewsService,
    private val database:       DBDatabase,
    private val query:          DBNewsQuery? = null,
    private val initialPage:    Int = 1
): RemoteMediator<Int, ArticleWithCategories>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
        return if (System.currentTimeMillis() - database.lastUpdated() >= cacheTimeout)
        {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.

                // write to database the timestamp
            database.writeUpdateTime()
            // refresh the data
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.

            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleWithCategories>
    ): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.next?.minus(1) ?: initialPage
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for prepend.
                val prevKey = remoteKeys?.prev
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with `endOfPaginationReached = false` because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its prevKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.next
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }


        try {

            val response = service.getArticles(
                paged = page,
                postPerPage = state.config.pageSize
            )

            val endOfPaginationReached = response.body()?.isEmpty() ?: true

            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {

                    // if fresh then delete all in the database
                    database.articleCategoryJoinDAO().deleteAll()
                    database.articleDAO().deleteAll()
                    database.categoryDAO().deleteAll()
                }
                val prevKey = if (page == initialPage) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                response.body()?.let {

                    insertToDB(database, it, prevKey, nextKey)

                }

            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticleWithCategories>): NewsRemoteKey? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { item ->
                // Get the remote keys of the last item retrieved
                database.withTransaction {

                    database.remoteKeyDAO().getRemoteKey(item.article.n_id)
                }

            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ArticleWithCategories>): NewsRemoteKey? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { item ->
                // Get the remote keys of the first items retrieved
                database.withTransaction {

                    database.remoteKeyDAO().getRemoteKey(item.article.n_id)
                }

            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ArticleWithCategories>
    ): NewsRemoteKey? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { item ->
                database.withTransaction {
                    database.remoteKeyDAO().getRemoteKey(item.article.n_id)
                }

            }
        }
    }

    companion object {

        fun insertToDB(
            db: DBDatabase,
            newsResponse: DBNewsResponse,
            prev: Int?,
            next: Int?) {

            val keys: MutableList<NewsRemoteKey> = mutableListOf()

            newsResponse.forEach { item ->

                val articleId = db.articleDAO().insertOrUpdate(
                    NewsItemDB(
                        n_id = 0,
                        post_id = item.post_id,
                        title = item.title,
                        description = item.description,
                        content = item.content,
                        author = item.author,
                        featured_image = item.featured_image,
                        format = item.format,
                        link = item.link,
                        slug = item.slug,
                        published_at = item.published_at,
                        updated_at = item.updated_at,
                        featured = item.featured?.let { it } ?: false,
                        videoURL = item.videoURL
                    )
                )

                keys.add(NewsRemoteKey(articleId, prev, next))

                val categoryIds: MutableList<Long> = mutableListOf()

                if (item.categories.isNotEmpty()) {

                    item.categories.forEach { category ->

                        val cat = CategoryDB(
                            c_id = 0,
                            category_id = category.category_id,
                            name = category.name
                        )

                        val id = db.categoryDAO().insertOrUpdate(cat)

                        categoryIds.add(id)
                    }
                }

                if (categoryIds.isNotEmpty()) {

                    categoryIds.forEach {  id ->

                        db.articleCategoryJoinDAO().insertOrUpdate(
                            ArticleCategoryJoin(
                                n_id = articleId,
                                c_id = id
                            )
                        )
                    }
                }

                categoryIds.clear()
            }

            db.remoteKeyDAO().insertAllRemoteKey(keys)

            keys.clear()

        }
    }
}

