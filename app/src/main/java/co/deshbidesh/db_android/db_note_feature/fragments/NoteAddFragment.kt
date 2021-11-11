package co.deshbidesh.db_android.db_note_feature.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteAddBinding
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment


class NoteAddFragment : DBBaseFragment() {

    private lateinit var toolbar: Toolbar

    override var bottomNavigationViewVisibility = View.GONE

    private var _binding: FragmentNoteAddBinding? = null

    private val binding get() = _binding!!

    private val sharedNoteDetailViewModel: DBNoteDetailViewModel by activityViewModels { DBNoteDetailViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoteAddBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init toolbar
        toolbar = binding.noteAddToolbar

        toolbar.inflateMenu(R.menu.note_save)

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){

                R.id.save_note_db -> {
                    saveNote()
                    true
                }
                else -> false
            }
        }

        // Focus on note content edit text text upon this fragment call
        binding.noteAddTitleEdittext.requestFocus()
        val inputMethodManager: InputMethodManager =
            this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        inputMethodManager.showSoftInput(binding.noteAddTitleEdittext, InputMethodManager.SHOW_IMPLICIT);

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    // Save note
    private fun saveNote(){
        val title = binding.noteAddTitleEdittext.text.toString()

        val content = binding.noteAddContentEdittext.text.toString()

        if (title.isNotBlank()) {

            sharedNoteDetailViewModel.addNote(title, content) {

                it?.let { id ->

                    sharedNoteDetailViewModel.getNote(id) { note ->

                        createNoteFinalize("Note Created.", note)
                    }
                }
            }

        } else {
            showToast("Please enter title.")
        }

    }

    private fun createNoteFinalize(msg: String, note: DBNote) {
        activity?.runOnUiThread {

            showToast(msg)

            val action = NoteAddFragmentDirections.actionNoteAddFragmentToNoteDetailFragment(note)

            findNavController().navigate(action)
        }
    }
}