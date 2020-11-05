package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteListBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteListPagingDataAdapter
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteListViewModelFactory
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteListViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.decorators.EqualSpaceItemDecorator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


class NoteListFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private lateinit var listViewModel: DBNoteListViewModel

    private lateinit var listViewModelFactory: DBNoteListViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNoteListBinding.inflate(inflater, container, false)

        context.let {

            binding.noteListToolbar.setNavigationOnClickListener {

                requireActivity().onBackPressed()
            }

            binding.noteListFab.setOnClickListener {

                findNavController().navigate(R.id.action_noteListFragment_to_noteAddFragment)
            }

            binding.noteListRecycleview.addItemDecoration(EqualSpaceItemDecorator(10))

            // layout for potrait and landscape
            binding.noteListRecycleview.layoutManager = GridLayoutManager(it, resources.getInteger(R.integer.grid_column_count))

            val adapter = DBNoteListPagingDataAdapter()

            binding.noteListRecycleview.adapter = adapter

            listViewModelFactory = DBNoteListViewModelFactory(DBNoteRepository(DBDatabase.getDatabase(it!!).noteDAO()))

            listViewModel = ViewModelProvider(this, listViewModelFactory).get(DBNoteListViewModel::class.java)

            suscribeUI(adapter)
        }

        return binding.root
    }

    private fun suscribeUI(adapter: DBNoteListPagingDataAdapter) {

        lifecycleScope.launch {

            listViewModel.getPagedList.distinctUntilChanged().collectLatest { it ->

                it.let {

                    adapter.submitData(it)
                }
            }
        }
    }
}