package co.deshbidesh.db_android.db_note_feature.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteAddBinding
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.main.MainActivity


class NoteAddFragment : DBBaseFragment() {

    private lateinit var toolbar: Toolbar

    override var bottomNavigationViewVisibility = View.GONE

    private var _binding: FragmentNoteAddBinding? = null

    private val binding get() = _binding!!

    private val sharedNoteDetailViewModel: DBNoteDetailViewModel by activityViewModels { DBNoteDetailViewModelFactory }

    private var keyboardHolderActivity: MainActivity? = null

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

        inisilize()

        setupToolbar()

        setupSwitch()
    }

    private fun inisilize() {
        // reference for main activity
        keyboardHolderActivity = activity as MainActivity

        keyboardHolderActivity?.registerEditText(binding.noteAddTitleEdittext)

        keyboardHolderActivity?.registerEditText(binding.noteAddContentEdittext)
    }

    private fun setupToolbar() {
        // Init toolbar
        toolbar = binding.noteAddToolbar

        toolbar.inflateMenu(R.menu.note_save)

        toolbar.setNavigationOnClickListener {

            hideAnyKeyboard()

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
    }

    private fun setupSwitch() {
        // on start set the ischeck to false
        binding.noteAddToolbarSwitch.isChecked = false

        binding.noteAddToolbarSwitch.setOnCheckedChangeListener { _ , isChecked ->

            keyboardHolderActivity?.showNepaliKeyboard = isChecked

            if (isChecked) {

                if (keyboardHolderActivity?.systemKeyboard == true) {

                    keyboardHolderActivity?.handleKeyboard()
                }

                binding.noteAddTitleEdittext.hint = " शीर्षक... "

                binding.noteAddContentEdittext.hint = " प+्+र=प्र, च+्+च=च्च, र+्+प=र्प ..."
            } else {

                if (keyboardHolderActivity?.isCustomKeyboardVisible() == true) {

                    keyboardHolderActivity?.showSystemKeyboard()
                }

                keyboardHolderActivity?.hideCustomKeyboard()

                binding.noteAddTitleEdittext.hint = " title... "

                binding.noteAddContentEdittext.hint = " content..."
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        keyboardHolderActivity = null

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

            hideAnyKeyboard()

            val action = NoteAddFragmentDirections.actionNoteAddFragmentToNoteDetailFragment(note)

            findNavController().navigate(action)
        }
    }

    private fun hideAnyKeyboard() {

        keyboardHolderActivity?.showNepaliKeyboard = false

        keyboardHolderActivity?.hideCustomKeyboard()

        keyboardHolderActivity?.hideSystemKeyboard()
    }
}