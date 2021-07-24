package co.deshbidesh.db_android.shared.utility

class DBNewsConstants {

    companion object {

        const val POST_TYPE = "post_type"

        const val POSTS_PER_PAGE = "posts_per_page"

        const val PAGE_NUMBER = "paged"

        const val ORDER_BY = "orderby"

        const val IN_ORDER_OF = "order"

        const val PAGE_SIZE = 40

        fun lastUpdated(): Long {
            return System.currentTimeMillis()
        }
    }
}