package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment


class NoteDetailFragment : DBBaseFragment() {

    //private val args by navArgs<NoteDetailFragmentArgs>()

    private lateinit var viewModel: DBNoteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.note_detail_toolbar)

        //val titleToolBar = view.findViewById<TextView>(R.id.note_detail_toolbar_title)

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        val titleTV = view.findViewById<TextView>(R.id.note_detail_title)

        val contentTV = view.findViewById<TextView>(R.id.note_detail_content)

        val editButton = view.findViewById<Button>(R.id.note_detail_edit_button)

        val deleteButton = view.findViewById<Button>(R.id.note_detail_delete_button)

        viewModel = ViewModelProvider(this).get(DBNoteViewModel::class.java)

        editButton.setOnClickListener {

            //val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToNoteEditFragment(args.noteDetail)
            //findNavController().navigate(action)
        }

        deleteButton.setOnClickListener {

            //viewModel.deleteNote(args.noteDetail)

            showToast("Note deleted")

            //findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
        }

        //titleTV.text = args.noteDetail.title

        //contentTV.text = args.noteDetail.content
    }

    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

}