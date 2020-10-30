package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteRecyclerAdapter
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.decorators.EqualSpaceItemDecorator
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NoteListFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private  lateinit var adapter: DBNoteRecyclerAdapter

    private lateinit var viewModel: DBNoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.note_list_toolbar)

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        val noteRecycleView = view.findViewById<RecyclerView>(R.id.note_list_recycleview)

        val fab = view.findViewById<FloatingActionButton>(R.id.note_list_fab)

        fab.setOnClickListener {

            findNavController().navigate(R.id.action_noteListFragment_to_noteAddFragment)
        }

        adapter = DBNoteRecyclerAdapter()

        viewModel = ViewModelProvider(this).get(DBNoteViewModel::class.java)

        //val topSpacingItemDecoration =  TopSpacingItemDecoration(30)
        val equalSpaceDecoration = EqualSpaceItemDecorator(10)

        noteRecycleView.addItemDecoration(equalSpaceDecoration)

        // layout for potrait and landscape
        val gridColumnCount = resources.getInteger(R.integer.grid_column_count)

        noteRecycleView.layoutManager = GridLayoutManager(context, gridColumnCount)

        noteRecycleView.adapter = adapter

        viewModel.readAllNotes.observe(viewLifecycleOwner, Observer { list ->

            adapter.loadData(list)
        })
    }

}