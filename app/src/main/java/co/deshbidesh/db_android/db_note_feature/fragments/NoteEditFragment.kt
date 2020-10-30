package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.hideKeyboard
import java.util.*


class NoteEditFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteEditFragmentArgs>()

    private lateinit var viewModel: DBNoteViewModel

    private lateinit var titleTV: EditText

    private lateinit var contentTV: EditText

    private var textChanged: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.note_edit_toolbar)

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        titleTV = view.findViewById(R.id.note_edit_title_edittext)

        contentTV = view.findViewById(R.id.note_edit_content_edittext)

        val saveButton = view.findViewById<Button>(R.id.note_edit_save_button)

        titleTV.setText(args.noteEdit.title)

        contentTV.setText(args.noteEdit.content)

        viewModel = ViewModelProvider(this).get(DBNoteViewModel::class.java)

        titleTV.doAfterTextChanged {

            textChanged = true
        }

        contentTV.doAfterTextChanged {

            textChanged = true
        }

        saveButton.setOnClickListener {

            hideKeyboard()

            val title: String = titleTV.text.toString()

            val content: String = contentTV.text.toString()

            if (!textfieldEmpty(title, content)) {

                if (textChanged) {

                    val note = DBNote(args.noteEdit.id,
                        title,
                        generateDescription(contentTV.text.toString()),
                        contentTV.text.toString(),
                        args.noteEdit.createdDate, Date(),
                        args.noteEdit.languageType)

                    viewModel.updateNote(note)

                    showToast("Note updated")

                    findNavController().navigate(R.id.action_noteEditFragment_to_noteListFragment)
                }
                else {

                    showToast("Nothing changed")
                }
            }
        }
    }

    private fun textfieldEmpty(title: String, content: String): Boolean {

        var msg: String = ""

        var msgEmpty: Boolean = false

        if (TextUtils.isEmpty(title)) {

            msg += "Title"

            msgEmpty = true
        }

        if (TextUtils.isEmpty(content)) {

            if (msg == "") {

                msg += "Note"

            } else {

                msg = "$msg and Note"
            }

            msgEmpty = true
        }

        if (msgEmpty) {

            showToast("$msg cannot be Empty.")
        }


        return msgEmpty
    }

    private fun generateDescription(content: String): String {

        var stringArray: List<String> = content.split(" ")

        var description: String = ""

        if (stringArray.count() > 15) {

            description = stringArray.take(15).joinToString(separator = " ") {it ->
                "$it"
            }
        } else {

            description = stringArray.joinToString(separator = " ") {it ->
                "$it"
            }
        }
        return  description
    }

    private fun showToast(message: String) {

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

}