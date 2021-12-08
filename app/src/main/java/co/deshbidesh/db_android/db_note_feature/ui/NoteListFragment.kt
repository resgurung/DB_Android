package co.deshbidesh.db_android.db_note_feature.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteListBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_note_feature.adapters.DBExpandableListAdapter
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteInfiniteAdapter
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteListViewModelFactory
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteDateRepresentable
import co.deshbidesh.db_android.db_note_feature.models.DBNoteInfiniteAdapterModel
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteListViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.widget.ExpandableListView
import android.widget.ExpandableListAdapter
import androidx.fragment.app.activityViewModels
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.infinite_list.*
import co.deshbidesh.db_android.db_note_feature.models.DBNoteWrapper
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteUIiState


class NoteListFragment :
    DBBaseFragment(),
    SearchView.OnQueryTextListener,
    SearchView.OnCloseListener,
    LoadMore,
    ItemTrackListener {

    override var bottomNavigationViewVisibility = View.GONE

    private lateinit var toolbar: Toolbar

    private var _binding: FragmentNoteListBinding? = null

    private val binding get() = _binding!!

    private lateinit var searchView: SearchView

//    private lateinit var listViewModel: DBNoteListViewModel
//
//    private lateinit var listViewModelFactory: DBNoteListViewModelFactory

    private lateinit var infiniteAdapter: DBNoteInfiniteAdapter

    private lateinit var layoutManager: LinearLayoutManager

    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    private var expandableListView: ExpandableListView? = null

    private var expandableListAdapter: ExpandableListAdapter? = null

    private val listViewModel: DBNoteDetailViewModel by activityViewModels { DBNoteDetailViewModelFactory }

    var listExpandedClick: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoteListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup toolbar
        setupToolbar()
        // setup rest of the view
        setupView()
        // register click for fab
        registerClickListener()
    }

    override fun onStart() {
        super.onStart()

        prepareDataForView()
    }

//    override fun onResume() {
//        super.onResume()
//
//        if (listViewModel.isEdited) {
//
//            listViewModel.isEdited = false
//
//            reloadData()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null

//        listViewModel.resetLoadModel(ColumnName.CREATED_DATE)
//
//        listViewModel.isEdited = false
//
//        listViewModel.resetExpandableModel()
    }

    private fun initViewModel() {
//        listViewModelFactory =
//            DBNoteListViewModelFactory(DBNoteRepository( DBDatabase.getDatabase(requireContext())))
//
//        listViewModel =
//            ViewModelProvider(this, listViewModelFactory).get(DBNoteListViewModel::class.java)
    }

    /*
        Toolbar setup
    */
    private fun setupToolbar() {

        toolbar = binding.noteListToolbar

        toolbar.inflateMenu(R.menu.db_note_list_menu)

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){

                R.id.note_list_search -> {

                    true
                }
                R.id.note_list_sort_by_created_date -> {

                    listViewModel.uiState = DBNoteUIiState.RECYCLERVIEW

                    setupView()

                    defaultState(ColumnName.CREATED_DATE, false)
                    true
                }
                R.id.note_list_sort_by_updated_date -> {

                    listViewModel.uiState = DBNoteUIiState.RECYCLERVIEW

                    setupView()

                    defaultState(ColumnName.UPDATED_DATE, false)

                    true
                }
                R.id.note_list_sort_by_year_date -> {

                    binding.noteLisyProgressBar.isVisible = true

                    listViewModel.uiState = DBNoteUIiState.EXPANDABLEVIEW

                    setupView()

                    listViewModel.searchGroupByYear()

                    true
                }
                else -> false
            }
        }

        toolbar.menu.apply {

            searchView = findItem(R.id.note_list_search).actionView as SearchView

            searchView.setOnQueryTextListener(this@NoteListFragment)

            searchView.setOnCloseListener(this@NoteListFragment)

            searchView.setOnQueryTextFocusChangeListener{ view,hasFocus ->

                if (hasFocus) {
                    searchStateBySearchView()
                }
            }
        }

        when(listViewModel.uiState) {

            DBNoteUIiState.SEARCHVIEW -> {
                binding.noteListToolbarTitle.isVisible = false
            } else -> {}
        }
    }

    /*
        setup view for current state
     */
    private fun setupView() {

        when(listViewModel.uiState) {

            DBNoteUIiState.SEARCHVIEW -> {
                setupUiForSearchViewState()
            }
            DBNoteUIiState.RECYCLERVIEW -> {
                setupUiForRecyclerViewState()
            }
            DBNoteUIiState.EXPANDABLEVIEW -> {
                setupUiForExpandableViewState()
            }
        }
    }

    /*
        Prepare Data For View
     */
    private fun prepareDataForView() {

        when(listViewModel.uiState) {

            DBNoteUIiState.SEARCHVIEW -> {

                listViewModel.resetLoadModel(ColumnName.CREATED_DATE)

                setupView()

                listViewModel.submitQuery(listViewModel.queryString)

                listViewModel.queryString = null
            }
            DBNoteUIiState.RECYCLERVIEW -> {

                if (listViewModel.isEdited) {

                    listViewModel.isEdited = false

                    reloadData()

                } else {

                    listViewModel.prepareDataForAdapter(
                        true,
                        listViewModel.loadMoreModel.originalList,
                        executorService
                    ) {

                        updateRecycleView(it)
                    }
                }

            }
            DBNoteUIiState.EXPANDABLEVIEW -> {

                updateExpandableListView()
            }
        }

        observeData()
    }

    /*
        Data change Observer
     */
    private fun observeData() {

        listViewModel.notesInfiniteList.observe(viewLifecycleOwner, { wrapper ->

            wrapper.getContentIfNotHandled()?.let { listWrapper ->

                when(listViewModel.uiState) {

                    DBNoteUIiState.SEARCHVIEW -> {

                        processDataForRecyclerView(listWrapper)
                    }
                    DBNoteUIiState.RECYCLERVIEW -> {

                        processDataForRecyclerView(listWrapper)
                    }
                    DBNoteUIiState.EXPANDABLEVIEW -> {

                        processDataForExpandedView(listWrapper)
                    }
                }
            } ?: run {
                // turn the loading off
                binding.noteLisyProgressBar.isVisible = false
            }
        })
    }

    /*
        Process Data
     */
    private fun processDataForExpandedView(listOfWrapper: List<DBNoteWrapper>) {

        val months = mutableListOf<DBNoteDateRepresentable>()

        var year: Int = 0

        for(item in listOfWrapper) {

            when(item.type) {

                DBNoteDataType.YEAR_COUNT_TYPE -> {

                    item.dateRepresentable?.let { dateItem ->

                        if (!listViewModel.loadExpandableModel.yearList.contains(dateItem)) {

                            listViewModel.loadExpandableModel.yearList.add(dateItem)

                            listViewModel.loadExpandableModel.monthHashMap[dateItem.dateInt] = mutableListOf()
                        }

                    }
                }
                DBNoteDataType.MONTH_COUNT_TYPE -> {

                    if (item.year != null && item.dateRepresentable != null) {
                        year = item.year!!
                        months.add(item.dateRepresentable!!)
                    }

                }
                else -> {}
            }
        }

        if (year != 0) {

            listViewModel.loadExpandableModel.monthHashMap.replace(year, months)

        }
        val data = listViewModel.loadExpandableModel

        updateExpandableListView()
    }

    private fun processDataForRecyclerView(
        listOfWrapper: List<DBNoteWrapper>
    ) {

        listViewModel.prepareOriginalListForAdapter(listOfWrapper)

        listViewModel.prepareDataForAdapter(
            true,
            listViewModel.loadMoreModel.originalList,
            executorService)
        {

            updateRecycleView(it)
        }
    }

    /*
        Search View delegate methods
     */
    // OnQueryTextListener
    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        searchFocusedState(newText)

        return false
    }

    private fun searchFocusedState(newText: String?) {

            when(listViewModel.uiState) {

                DBNoteUIiState.SEARCHVIEW -> {

                    listViewModel.resetLoadModel(ColumnName.CREATED_DATE)

                    listViewModel.queryString = newText

                    listViewModel.submitQuery(newText)
                }
                else -> {}
            }
    }

    // OnCloseListener
    override fun onClose(): Boolean {

        defaultState(ColumnName.CREATED_DATE,true)

        return false
    }

    // fold searchView to initial state
    private fun foldSearchView() {
        if (!searchView.isIconified) {
            searchView.setQuery("", false)
            searchView.onActionViewCollapsed()
        }
    }
    // load more

    /*
        Interface override methods
     */
    override fun noteItemClick(item: DBNote, position: Int) {

        if (position <= listViewModel.loadMoreModel.originalList.size) {
            listViewModel.selectedItem = position
        }

        val action =
            NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(item)

        findNavController().navigate(action)

        //foldSearchView()
    }

    override fun loadMore(
        previousList: MutableList<DBNoteInfiniteAdapterModel>,
        loadMoreModel: LoadMoreModel
    ) {

        binding.noteLisyProgressBar.isVisible = true

        removeLastElement(previousList)

        when(listViewModel.uiState) {

            DBNoteUIiState.SEARCHVIEW -> {
                listViewModel.submitQuery(listViewModel.queryString)
            }
            DBNoteUIiState.RECYCLERVIEW -> {
                listViewModel.notesInfiniteList(loadMoreModel.searchColumn)
            }
            DBNoteUIiState.EXPANDABLEVIEW -> {}
        }
    }

    /*
        search operation
     */
    private fun searchStateBySearchView() {

        listViewModel.uiState = DBNoteUIiState.SEARCHVIEW

        setupView()

        searchView.setQuery("", false)

        listViewModel.resetLoadModel(ColumnName.CREATED_DATE)

        binding.noteListAddFab.isVisible = false

        binding.noteListCrossFab.isVisible = true

        binding.noteListToolbarTitle.isVisible = false

        listViewModel.prepareDataForAdapter(
            false,
            listViewModel.loadMoreModel.originalList,
            executorService
        ) {

            updateRecycleView(it)
        }
    }

    private fun defaultState(columnName: ColumnName, closeButtonPress: Boolean) {

        listViewModel.resetLoadModel(columnName)

        binding.noteListAddFab.isVisible = true

        binding.noteListCrossFab.isVisible = false

        binding.noteListToolbarTitle.isVisible = true

        if (!closeButtonPress) {
            foldSearchView()
        }

        listViewModel.prepareDataForAdapter(
            false,
            listViewModel.loadMoreModel.originalList,
            executorService
        ) {

            updateRecycleView(it)
        }

        when(columnName) {
            ColumnName.CREATED_DATE -> {
                listViewModel.notesInfiniteList(columnName = columnName)
            }
            ColumnName.UPDATED_DATE -> {
                listViewModel.searchByUpdatedDate()
            }
        }

    }

    /*
        View hide/show
     */
    private fun hideRecycleView() {

        binding.noteListRecycleview.isVisible = false

        binding.expendableList.isVisible = true
    }

    private fun hideExpandableView() {

        binding.noteListRecycleview.isVisible = true

        binding.expendableList.isVisible = false
    }

    /*
        View Setup
     */
    private fun setupUiForRecyclerViewState() {

        setupRecyclerView()
    }

    private fun setupUiForSearchViewState() {

        setupRecyclerView()

        binding.noteListToolbarTitle.isVisible = false
    }

    private fun setupUiForExpandableViewState() {

        hideRecycleView()

        binding.noteLisyProgressBar.isVisible = false

        expandableListAdapter = DBExpandableListAdapter(
            requireContext(),
            mutableListOf(),
            hashMapOf()
        )

        expandableListView = binding.expendableList

        expandableListView?.setAdapter(expandableListAdapter)

        registerExpandableListTouchListener(expandableListView)

        moveToPositionOnExpandableListAdapter()
    }

    private fun setupRecyclerView(){

        infiniteAdapter = DBNoteInfiniteAdapter(mutableListOf())

        infiniteAdapter.itemTrackListener = this@NoteListFragment

        layoutManager = GridLayoutManager(requireContext(), 1)

        hideExpandableView()

        binding.noteListRecycleview.layoutManager = layoutManager

        binding.noteListRecycleview.adapter = infiniteAdapter

        binding.noteListRecycleview.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    /*
        Update ListView
     */
    // expandable updater
    private fun updateExpandableListView() {

        binding.noteLisyProgressBar.isVisible = false

        expandableListAdapter = DBExpandableListAdapter(
            requireContext(),
            listViewModel.loadExpandableModel.yearList,
            listViewModel.loadExpandableModel.monthHashMap
        )

        expandableListView = binding.expendableList

        expandableListView?.setAdapter(expandableListAdapter)

        registerExpandableListTouchListener(expandableListView)

        moveToPositionOnExpandableListAdapter()
    }

    // recyclerview updater
    private fun updateRecycleView(
        infiniteList: MutableList<DBNoteInfiniteAdapterModel>
    ) {

        activity?.runOnUiThread {

            infiniteAdapter.infiniteList = infiniteList

            infiniteAdapter.setFiniteList(
                listViewModel.loadMoreModel,
                this@NoteListFragment
            )

            listViewModel.selectedItem?.let {
                moveToPosition(it)
            }
            infiniteAdapter.notifyDataSetChanged()

            binding.noteLisyProgressBar.isVisible = false
        }
    }

    /*
        Register Clicklistener
     */
    // expandable view
    private fun registerExpandableListTouchListener(expandableListView: ExpandableListView?) {
        expandableListView?.setOnGroupExpandListener { groupPosition ->

            val item = listViewModel.loadExpandableModel.yearList[groupPosition]

            val list = listViewModel.loadExpandableModel.monthHashMap[item.dateInt]

            if (list?.isEmpty() == true) {

                listViewModel.expandedPositionOnExpandableList = groupPosition

                listViewModel.searchGroupByMonth(item.dateInt)
            }

        }
        expandableListView?.setOnGroupCollapseListener { groupPosition ->
            Toast.makeText(
                requireContext(),
                "${listViewModel.loadExpandableModel.yearList[groupPosition]}  List Collapsed.",
                Toast.LENGTH_SHORT
            ).show()
        }
        expandableListView?.setOnChildClickListener { _, _, groupPosition, childPosition, _ ->

            val year = listViewModel.loadExpandableModel.yearList[groupPosition].dateInt
            val month = listViewModel.loadExpandableModel.monthHashMap[listViewModel.loadExpandableModel.yearList[groupPosition].dateInt]?.get(childPosition)!!.dateInt
            listViewModel.searchNotesByYearAndMonth(year, month) {
                activity?.runOnUiThread {
                    val action = NoteListFragmentDirections.actionNoteListFragmentToDBNoteMonthFragment(year, month, it.toTypedArray())
                    findNavController().navigate(action)
                }
            }

            false
        }
    }

    // recycler view
    private fun registerClickListener() {

        binding.noteListAddFab.setOnClickListener {

            findNavController().navigate(R.id.action_noteListFragment_to_noteAddFragment)
        }

        binding.noteListCrossFab.setOnClickListener {
            //
            defaultState(ColumnName.CREATED_DATE, false)
        }
    }

    /*
        Move to position / remove last item
     */
    // expandable View
    private fun moveToPositionOnExpandableListAdapter() {

        listViewModel.expandedPositionOnExpandableList?.let{

            expandableListView?.expandGroup(it)

            listViewModel.expandedPositionOnExpandableList = null
        }
    }

    // recycler view
    private fun moveToPosition(position: Int) {
        layoutManager.scrollToPositionWithOffset(position, 0)
        listViewModel.selectedItem = null
    }

    private fun removeLastElement(list: MutableList<DBNoteInfiniteAdapterModel>) {

        list.removeAt(list.size -1)

        val scrollPosition = list.size

        infiniteAdapter.notifyItemRemoved(scrollPosition)
    }

    private fun reloadData() {

        defaultState(ColumnName.CREATED_DATE, false)
    }
}
