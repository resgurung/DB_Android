package co.deshbidesh.db_android.db_note_feature.infinite_list

import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteDateRepresentable
import co.deshbidesh.db_android.db_note_feature.models.DBNoteInfiniteAdapterModel
import co.deshbidesh.db_android.db_note_feature.models.DBNoteWrapper

interface LoadMore {

    fun loadMore(
        previousList: MutableList<DBNoteInfiniteAdapterModel>,
        loadMoreModel: LoadMoreModel
    )
}

interface ItemTrackListener {

    fun noteItemClick(item: DBNote, position: Int)
}

enum class DBNoteDataType {
    NOTE_TYPE, YEAR_COUNT_TYPE, MONTH_COUNT_TYPE
}

data class LoadMoreModel(
    var startPosition: Int,
    var endPosition: Int,
    var originalList: MutableList<DBNoteWrapper>,
    var currentPage: Int,
    var type: DBNoteDataType,
    var searchColumn: ColumnName
)

data class LoadExpandableModel(
    var yearList: MutableList<DBNoteDateRepresentable>,
    var monthHashMap: HashMap<Int, MutableList<DBNoteDateRepresentable>>
)

enum class ColumnName(val column : String) {
    CREATED_DATE("createdDate"),
    UPDATED_DATE("updatedDate")
}