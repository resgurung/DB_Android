package co.deshbidesh.db_android.db_network.db_services

import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsResponse
import co.deshbidesh.db_android.shared.utility.DBNewsConstants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


typealias DBNewsQuery = MutableMap<String, Any?>

interface DBNewsService {

    /*

    GET URL parameter

        POST TYPE (type of post)
        key: post_type
        value: post
        if empty default is 'post'

        POST PER PAGE (number of items per page)
        key: post_per_page
		value: number(Int)
		if empty default is -1(all)

		PAGED(page number)
		key: paged
		value: 1/2/3/4
		if empty default is 1

		ORDER BY
		key: orderby
		value: date/name
		if empty default is date

	    ORDER
	    key: order
	    value: DESC/ASC
	    if empty default is DESC

     */
    @GET("db/v1/posts")
    suspend fun getArticles(
        @Query(DBNewsConstants.POST_TYPE)
        post: String = "",
        @Query(DBNewsConstants.POSTS_PER_PAGE)
        postPerPage: Int,
        @Query(DBNewsConstants.PAGE_NUMBER)
        paged: Int,
        @Query(DBNewsConstants.ORDER_BY)
        orderBy: String = "",
        @Query(DBNewsConstants.IN_ORDER_OF)
        order: String = ""
    ): Response<DBNewsResponse>
}