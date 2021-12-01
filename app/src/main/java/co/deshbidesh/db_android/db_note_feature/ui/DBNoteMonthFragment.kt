package co.deshbidesh.db_android.db_note_feature.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbNoteMonthBinding
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteMonthAdapter
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.utility.DBCalenderMonth

class DBNoteMonthFragment : DBBaseFragment(), DBNoteMonthAdapter.DBNoteInterfaceItemClick {

    override var bottomNavigationViewVisibility = View.GONE

    private lateinit var toolbar: Toolbar

    private var _binding: FragmentDbNoteMonthBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: DBNoteMonthAdapter

    private lateinit var layoutManager: LinearLayoutManager

    private val args by navArgs<DBNoteMonthFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbNoteMonthBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inisilize()

        setupToolbar()

        setupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun inisilize() {

        layoutManager = GridLayoutManager(requireContext(), 1)

        args.notes?.let {
            adapter = DBNoteMonthAdapter(it.toMutableList())
            adapter.itemClicked = this@DBNoteMonthFragment
        }

    }

    private fun setupToolbar() {

        toolbar = binding.noteListMonthToolbar

        binding.noteListMonthToolbarTitle.text = "${args.year} ${DBCalenderMonth.months[args.month]}"

        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {

        binding.noteListMonthRecycleview.layoutManager = layoutManager

        binding.noteListMonthRecycleview.adapter = adapter

        binding.noteListMonthRecycleview.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    override fun itemClicked(item: DBNote) {

        val action = DBNoteMonthFragmentDirections.actionDBNoteMonthFragmentToNoteDetailFragment(item)
        findNavController().navigate(action)
    }
}