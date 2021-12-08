package co.deshbidesh.db_android.db_note_feature.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_note_feature.infinite_list.ColumnName
import co.deshbidesh.db_android.db_note_feature.infinite_list.DBNoteDataType
import co.deshbidesh.db_android.db_note_feature.infinite_list.LoadExpandableModel
import co.deshbidesh.db_android.db_note_feature.infinite_list.LoadMoreModel
import co.deshbidesh.db_android.db_note_feature.models.*
import co.deshbidesh.db_android.db_note_feature.repository.DBImageRepository
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.utility.DBLiveDataEventWrapper
import co.deshbidesh.db_android.shared.utility.DBNoteConstant
import co.deshbidesh.db_android.shared.utility.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import java.util.concurrent.Executor

enum class DBNoteUIiState {
    SEARCHVIEW, RECYCLERVIEW, EXPANDABLEVIEW
}

class DBNoteDetailViewModel(
    private val noteRepository: DBNoteRepository,
    private val imageRepository: DBImageRepository
): ViewModel() {


    var isEdited: Boolean = false

    /* variable for list fragments */
    var loading: MutableLiveData<Boolean> = MutableLiveData()

    private val _notesInfiniteList = MutableLiveData<DBLiveDataEventWrapper<List<DBNoteWrapper>>>()

    val  notesInfiniteList: LiveData<DBLiveDataEventWrapper<List<DBNoteWrapper>>> = _notesInfiniteList

    var loadMoreModel: LoadMoreModel

    var loadExpandableModel: LoadExpandableModel

    var selectedItem: Int? = null

    var queryString: String? = null

    var expandedPositionOnExpandableList: Int? = null

    var uiState: DBNoteUIiState = DBNoteUIiState.RECYCLERVIEW

    /* variable for detail fragment / add fragment */
    lateinit var fileUtils: FileUtils

    var currentPair: Pair<Bitmap?, File?>? = null

    private var _images: MutableLiveData<List<DBImage>> = MutableLiveData()

    val images: LiveData<List<DBImage>>
        get() = _images

    // init
    init{

        loadMoreModel = createLoadModel()

        loadExpandableModel = createLoadExpandableModel()

        notesInfiniteList(ColumnName.CREATED_DATE)
    }

    /* methods for list fragment */
    fun notesInfiniteList(columnName: ColumnName) {

        loading.postValue(true)

        viewModelScope.launch(Dispatchers.IO) {
            val infiniteList = noteRepository.getNoteList(
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

    fun resetLoadModel( columnName: ColumnName) {

        loadMoreModel.apply {
            startPosition = 0
            endPosition = 0
            originalList = mutableListOf()
            currentPage = DBNoteConstant.INITIAL_PAGE_INDEX
            searchColumn = columnName
        }
    }

    private fun createLoadExpandableModel(): LoadExpandableModel {

        return LoadExpandableModel(mutableListOf(), hashMapOf())
    }

    fun resetExpandableModel() {

        loadExpandableModel.yearList.clear()
        loadExpandableModel.monthHashMap.clear()
    }


    fun submitQuery(query: String?) {

        query?.let { wrappedQuery ->

            if (wrappedQuery != "") {
                viewModelScope.launch(Dispatchers.IO) {

                    val infiniteSearchList = noteRepository.search(
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

        viewModelScope.launch(Dispatchers.IO) {

            val infiniteList = noteRepository.getNoteListByUpdatedDate(
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

        viewModelScope.launch(Dispatchers.IO) {

            val monthList = noteRepository.groupByMonth("createdDate", year)

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

        viewModelScope.launch(Dispatchers.IO) {

            val yearList = noteRepository.groupByYear("createdDate")

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

        viewModelScope.launch(Dispatchers.IO) {

            val monthNoteList = noteRepository.getMonthNotes(
                "createdDate",
                year,
                month)
            callback(monthNoteList)
        }
    }

    /* Methods for detail fragment */
    fun getNote(id: Long, listener: (DBNote) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            listener(noteRepository.singleNote(id))
        }
    }

    fun getImagesFromDatabase(id: Int) {
        viewModelScope.launch (Dispatchers.IO){
            val dbImages = imageRepository.getImageListByNoteId(id)
            _images.postValue(dbImages)
        }
    }


    fun addNote(
        title: String,
        content: String,
        listener: (Long?) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            val note = DBNote(
                0,
                title,
                DBHelper.generateDescriptionFromContent(content),
                content,
                null,
                Date(),
                Date(),
                DBNoteLanguage.ENGLISH
            )

            val noteId = noteRepository.addNote(note)

            listener(noteId)
        }
    }

    fun updateNote(note: DBNote, listener: (Boolean) -> Unit) {

        note.description = DBHelper.generateDescriptionFromContent(note.content)

        note.updatedDate = Date()

        viewModelScope.launch(Dispatchers.IO) {

            noteRepository.updateNote(note)

            listener(true)
        }
    }

    fun addImage(noteId: Int, path: String, listener: () -> Unit) {

        if (noteId != 0) {

            viewModelScope.launch (Dispatchers.IO){

                val dbImage = DBImage(
                    0,
                    path,
                    noteId
                )

                imageRepository.addImage(dbImage)

                listener()
            }
        } else {
            listener()
        }

    }

    fun deleteImage(imgObj: DBNoteUIImage?, listener: (Boolean) -> Unit){

        imgObj?.let {

            viewModelScope.launch(Dispatchers.IO) {

                // remove the file object from the gallery
                fileUtils.deleteFile(it.imagePath)
                // remove the file row from the db
                imageRepository.deleteSingleImageById(it.id)
                // all went goo return true
                listener(true)
            }
        } ?: run {
            listener(false)
        }

    }

    fun clearCurrentPair() {
        // second is file on the cache dir, better to delete it by ourself rather
        // wait for system to clear
        if (currentPair?.second?.exists() == true) {
            currentPair?.second?.delete()
        }
        // nullify currentPair
        currentPair = null // this is bit map
    }

    fun deleteNote(note: DBNote, listener: (Boolean) -> Unit) {

        viewModelScope.launch(Dispatchers.IO) {

            // retrieve all images reference
            val imageList = imageRepository.getImageListByNoteId(note.id)
            // remove all image from gallery
            imageList.map { fileUtils.deleteFile(it.imgPath) }
            // delete all images reference from database
            imageRepository.deleteImagesByNoteId(note.id)
            // delete note
            noteRepository.delete(note)

            listener(true)
        }
    }

}