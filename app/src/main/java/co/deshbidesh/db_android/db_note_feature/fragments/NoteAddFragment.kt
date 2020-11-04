package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteAddBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteAddViewModelFactory
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteAddViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.hideKeyboard


class NoteAddFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private lateinit var addViewModel: DBNoteAddViewModel

    private lateinit var addViewModelFactory: DBNoteAddViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentNoteAddBinding.inflate(inflater, container, false)

        context.let {

            binding.noteAddToolbar.setNavigationOnClickListener {

                hideKeyboard()

                requireActivity().onBackPressed()
            }

            binding.noteAddSaveButton.setOnClickListener {

                addViewModel.title = binding.noteAddTitleEdittext.text.toString()

                addViewModel.content = binding.noteAddContentEdittext.text.toString()

                if (!addViewModel.isTextEmpty()) {

                    addViewModel.addNote()

                    hideKeyboard()

                    showToast("Successfully created a Note")

                    findNavController().navigate(R.id.action_noteAddFragment_to_noteListFragment)

                } else {
                    showToast("Fields cannot be empty")
                }
            }

            addViewModelFactory = DBNoteAddViewModelFactory(
                DBNoteRepository(
                    DBDatabase.getDatabase(it!!).noteDAO()),
                DBHelper())

            addViewModel = ViewModelProvider(this, addViewModelFactory).get(DBNoteAddViewModel::class.java)

        }

        return binding.root
    }

}