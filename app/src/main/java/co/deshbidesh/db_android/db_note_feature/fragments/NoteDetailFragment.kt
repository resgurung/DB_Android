package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteDetailBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment


class NoteDetailFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteDetailFragmentArgs>()

    private lateinit var noteDetailViewModel: DBNoteDetailViewModel

    private lateinit var noteDetailViewModelFactory: DBNoteDetailViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNoteDetailBinding.inflate(inflater, container, false)

        context ?: return binding.root

        noteDetailViewModelFactory = DBNoteDetailViewModelFactory(
            DBNoteRepository(
                DBDatabase.getDatabase(requireContext()).noteDAO()
            ),args.noteDetail)

        noteDetailViewModel = ViewModelProvider(this, noteDetailViewModelFactory).get(DBNoteDetailViewModel::class.java)

        binding.noteDetailToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        binding.noteDetailEditButton.setOnClickListener {

            val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToNoteEditFragment(noteDetailViewModel.getNote())

            findNavController().navigate(action)
        }

        binding.noteDetailDeleteButton.setOnClickListener {

            // delete here
            noteDetailViewModel.deleteNote()

            showToast("Note deleted")

            findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
        }

        binding.noteDetailTitle.text = noteDetailViewModel.getNote().title

        binding.noteDetailContent.text = noteDetailViewModel.getNote().content

        return binding.root
    }

}