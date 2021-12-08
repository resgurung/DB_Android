package co.deshbidesh.db_android.db_note_feature.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_note_feature.infinite_list.ColumnName
import co.deshbidesh.db_android.db_note_feature.infinite_list.DBNoteDataType
import co.deshbidesh.db_android.db_note_feature.infinite_list.LoadExpandableModel
import co.deshbidesh.db_android.db_note_feature.infinite_list.LoadMoreModel
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteInfiniteAdapterModel
import co.deshbidesh.db_android.db_note_feature.models.DBNoteWrapper
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.shared.utility.DBLiveDataEventWrapper
import co.deshbidesh.db_android.shared.utility.DBNoteConstant
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.concurrent.Executor



class DBNoteListViewModel(
    private val repository: DBNoteRepository
): ViewModel() {

    var loading: MutableLiveData<Boolean> = MutableLiveData()

    private val _notesInfiniteList = MutableLiveData<DBLiveDataEventWrapper<List<DBNoteWrapper>>>()

    val  notesInfiniteList: LiveData<DBLiveDataEventWrapper<List<DBNoteWrapper>>> = _notesInfiniteList

    var loadMoreModel: LoadMoreModel

    var loadExpandableModel: LoadExpandableModel

    var selectedItem: Int? = null

    var queryString: String? = null

    //var isSearching: Boolean = false

    var expandedPositionOnExpandableList: Int? = null

    var uiState: DBNoteUIiState = DBNoteUIiState.RECYCLERVIEW

    init{

        loadMoreModel = createLoadModel()

        loadExpandableModel = createLoadExpandableModel()

        notesInfiniteList(ColumnName.CREATED_DATE)
    }

    fun notesInfiniteList(columnName: ColumnName) {

        loading.postValue(true)

        viewModelScope.launch(IO) {
            val infiniteList = repository.getNoteList(
                index = loadMoreModel.currentPage
            )

            _notesInfiniteList.postValue(
                DBLiveDataEventWrapper(
                    infiniteList.map{ note ->
                        DBNoteWrapper(
                            note = note,
                            type = DBNoteDataType.NOTE_TYPE
                        )
                    }
                )
            )
        }
    }

    fun prepareDataForAdapter(
        loadMoreButton: Boolean,
        list: List<DBNoteWrapper>,
        executor: Executor,
        callback: (MutableList<DBNoteInfiniteAdapterModel>) -> Unit
    ) {

        executor.execute {

            val adapterModel = mutableListOf<DBNoteInfiniteAdapterModel>()

            for (element in list) {

                when(element.type) {
                    DBNoteDataType.NOTE_TYPE -> {
                        adapterModel.add(
                            DBNoteInfiniteAdapterModel(
                                DBNoteInfiniteAdapterModel.VIEW_TYPE_DATA,
                                element.note
                            )
                        )
                    }
                }
            }

            if (loadMoreButton) {

                adapterModel.add(
                    DBNoteInfiniteAdapterModel(
                        DBNoteInfiniteAdapterModel.VIEW_TYPE_PROGRESS,
                        null
                    )
                )
            }

            callback(adapterModel)
        }
    }

    fun prepareOriginalListForAdapter(list: List<DBNoteWrapper>) {

        loadMoreModel.startPosition = loadMoreModel.originalList.size
        loadMoreModel.originalList.addAll(list)
        loadMoreModel.endPosition = loadMoreModel.originalList.size
        loadMoreModel.currentPage += 1
    }

    private fun createLoadModel(): LoadMoreModel {

        return LoadMoreModel(
            0,
            DBNoteConstant.DB_NOTE_PAGE_SIZE,
            mutableListOf(),
            DBNoteConstant.INITIAL_PAGE_INDEX,
            DBNoteDataType.NOTE_TYPE,
            ColumnName.CREATED_DATE
        )
    }

    private fun createLoadExpandableModel(): LoadExpandableModel {

        return LoadExpandableModel(mutableListOf(), hashMapOf())
    }

    fun resetLoadModel( columnName: ColumnName) {

        loadMoreModel.apply {
            startPosition = 0
            endPosition = 0
            originalList = mutableListOf()
            currentPage = DBNoteConstant.INITIAL_PAGE_INDEX
            searchColumn = columnName
        }
    }

    fun submitQuery(query: String?) {

        query?.let { wrappedQuery ->

            if (wrappedQuery != "") {
                viewModelScope.launch(IO) {

                    val infiniteSearchList = repository.search(
                        wrappedQuery,
                        loadMoreModel.currentPage
                    )

                    _notesInfiniteList.postValue(
                        DBLiveDataEventWrapper(
                            infiniteSearchList.map{ note ->
                                DBNoteWrapper(
                                    note = note,
                                    type = DBNoteDataType.NOTE_TYPE
                                )
                            }
                        )
                    )
                }
            } else {
                _notesInfiniteList.postValue(
                    DBLiveDataEventWrapper(
                        emptyList()
                    )
                )
            }

        }
    }

    fun searchByUpdatedDate() {

        viewModelScope.launch(IO) {

            val infiniteList = repository.getNoteListByUpdatedDate(
                index = loadMoreModel.currentPage
            )

            _notesInfiniteList.postValue(
                DBLiveDataEventWrapper(
                    infiniteList.map{ note ->
                        DBNoteWrapper(
                            note = note,
                            type = DBNoteDataType.NOTE_TYPE
                        )
                    }
                )
            )
        }
    }

    fun searchGroupByMonth(year: Int){

        viewModelScope.launch(IO) {

            val monthList = repository.groupByMonth("createdDate", year)

            _notesInfiniteList.postValue(
                DBLiveDataEventWrapper(
                    monthList.map{ month ->
                        DBNoteWrapper(
                            dateRepresentable = month,
                            type = DBNoteDataType.MONTH_COUNT_TYPE,
                            year = year
                        )
                    }
                )
            )
        }
    }

    fun searchGroupByYear(){

        viewModelScope.launch(IO) {

            val yearList = repository.groupByYear("createdDate")

            _notesInfiniteList.postValue(
                DBLiveDataEventWrapper(
                    yearList.map{ year ->
                        DBNoteWrapper(
                            dateRepresentable = year,
                            type = DBNoteDataType.YEAR_COUNT_TYPE
                        )
                    }
                )
            )
        }
    }

    fun searchNotesByYearAndMonth(
        year: Int,
        month: Int,
        callback: (List<DBNote>)-> Unit
    ) {

        viewModelScope.launch(IO) {

            val monthNoteList = repository.getMonthNotes(
                "createdDate",
                year,
                month)
            callback(monthNoteList)
        }
    }
}