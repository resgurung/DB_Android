package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import android.text.TextUtils
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
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.models.DBNoteLanguage
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.hideKeyboard
import java.util.*


class NoteAddFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private lateinit var viewModel: DBNoteViewModel

    private lateinit var titleTV: TextView

    private lateinit var contentTV: TextView

    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.note_add_toolbar)

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        titleTV = view.findViewById(R.id.note_add_title_edittext)

        contentTV = view.findViewById(R.id.note_add_content_edittext)

        saveButton = view.findViewById(R.id.note_add_save_button)

        saveButton.setOnClickListener {

            save()
        }

        viewModel = ViewModelProvider(this).get(DBNoteViewModel::class.java)
    }

    private fun save() {

        val title = titleTV.text.toString()

        val content = contentTV.text.toString()

        if (!textfieldEmpty(title, content)) {

            hideKeyboard()

            val note = DBNote(0,
                title,
                generateDescription(content),
                content,
                Date(),
                Date(),
                DBNoteLanguage.ENGLISH)

            viewModel.addNote(note)

            showToast("Successfully created a Note")

            findNavController().navigate(R.id.action_noteAddFragment_to_noteListFragment)

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