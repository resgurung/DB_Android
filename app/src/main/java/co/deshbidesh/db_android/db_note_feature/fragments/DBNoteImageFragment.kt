package co.deshbidesh.db_android.db_note_feature.fragments


import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.note_utils.NotesImageUtils
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class DBNoteImageFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<DBNoteImageFragmentArgs>()

    private val sharedNoteDetailViewModel: DBNoteDetailViewModel by activityViewModels { DBNoteDetailViewModelFactory }

    private var doneButton: Button? = null
    private var cancelButton: Button? = null
    private var saveButton: Button? = null
    private var deleteButton: Button? = null
    private var fullscreenContent: ImageView? = null

    private lateinit var navBar: BottomNavigationView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_db_note_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {

            navBar = it.findViewById(R.id.note_detail_bottom_nav)

            navBar.isVisible = false
        }

        setupView(view)

        registerClickListener()
    }

    private fun setupView(view: View) {

        // inisilize button
        doneButton = view.findViewById(R.id.image_fullscreen_done_button) as Button

        cancelButton = view.findViewById(R.id.image_fullscreen_cancel_button) as Button

        saveButton = view.findViewById(R.id.image_fullscreen_save_button) as Button

        deleteButton = view.findViewById(R.id.image_fullscreen_delete_button) as Button

        fullscreenContent = view.findViewById(R.id.full_screen_img_view)

        args.settings?.let {
            // show hide buttons
            doneButton?.isVisible = it.doneButton

            cancelButton?.isVisible = it.cancelButton

            saveButton?.isVisible = it.saveButton

            deleteButton?.isVisible = it.deleteButton

            if (it.imagePath == "") {
                fullscreenContent?.setImageBitmap(null)
                fullscreenContent?.setImageBitmap(sharedNoteDetailViewModel.currentPair?.first)
            }else {
                fullscreenContent?.setImageURI(null) // remove previous cache if any
                fullscreenContent?.setImageURI(Uri.parse(it.imagePath))
            }
        }

    }

    private fun registerClickListener() {

        doneButton?.setOnClickListener {

            navigate(null)
        }

        cancelButton?.setOnClickListener {

            sharedNoteDetailViewModel.clearCurrentPair()

            navigate("operation cancelled.")
        }

        saveButton?.setOnClickListener {

            val dir = sharedNoteDetailViewModel.fileUtils.createDirectoryIfNotExist()

            val file = sharedNoteDetailViewModel.fileUtils.makeFile(
                dir,
                SimpleDateFormat(
                    NotesImageUtils.FILENAME_FORMAT,
                    Locale.UK
                ).format(
                    System.currentTimeMillis()
                ) + NotesImageUtils.EXTENSION
            )

            sharedNoteDetailViewModel.currentPair?.first?.let {
                sharedNoteDetailViewModel.fileUtils.writeImageToExternalStorage(file, it)
            }

            sharedNoteDetailViewModel.addImage(
                args.settings?.noteId ?: 0,
            file.path
            ) {

                sharedNoteDetailViewModel.clearCurrentPair()

                navigate("image saved.")
            }
        }

        deleteButton?.setOnClickListener {

            sharedNoteDetailViewModel.deleteImage(args.settings) {
                if (it) {
                    navigate("image deleted.")
                } else {
                    navigate("image delete failed.")
                }

            }
        }
    }

    private fun navigate(message: String?) {

        activity?.runOnUiThread {

            message?.let {
                showToast(it)
            }

            findNavController().popBackStack()
        }
    }
}