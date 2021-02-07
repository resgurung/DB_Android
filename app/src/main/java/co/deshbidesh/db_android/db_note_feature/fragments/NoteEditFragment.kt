package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteEditBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteEditViewModelFactory
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteEditViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.hideKeyboard


class NoteEditFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteEditFragmentArgs>()

    private lateinit var noteEditViewModel: DBNoteEditViewModel

    private lateinit var noteEditViewModelFactory: DBNoteEditViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNoteEditBinding.inflate(inflater, container, false)

        noteEditViewModelFactory = DBNoteEditViewModelFactory(
            DBNoteRepository(DBDatabase.getDatabase(requireContext()).noteDAO()),
            args.noteEdit, DBHelper()
        )

        noteEditViewModel = ViewModelProvider(this, noteEditViewModelFactory).get(DBNoteEditViewModel::class.java)

        binding.noteEditToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        binding.noteEditSaveButton.setOnClickListener {

            noteEditViewModel.title = binding.noteEditTitleEdittext.text.toString()

            noteEditViewModel.content = binding.noteEditContentEdittext.text.toString()

            if (!noteEditViewModel.isTextEmpty()) {

                if (!noteEditViewModel.isSame()) {

                    noteEditViewModel.updateNote()

                    hideKeyboard()

                    showToast("Note updated")

                    findNavController().navigate(R.id.action_noteEditFragment_to_noteListFragment)

                } else {

                    showToast("Please edit one of the field")
                }
            } else {

                showToast("Fields cannot be empty")
            }
        }

        binding.noteEditTitleEdittext.setText(noteEditViewModel.getNote().title)

        binding.noteEditContentEdittext.setText(noteEditViewModel.getNote().content)

        return binding.root
    }
}