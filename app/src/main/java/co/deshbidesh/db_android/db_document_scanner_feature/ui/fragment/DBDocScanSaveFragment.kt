package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbDocScanSaveBinding
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanSaveObject
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.SharedViewModel
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.note_utils.NotesImageUtils
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.extensions.hasPermission
import co.deshbidesh.db_android.shared.extensions.showAlert
import co.deshbidesh.db_android.shared.hideKeyboard
import co.deshbidesh.db_android.shared.utility.DBPermissionConstants
import co.deshbidesh.db_android.shared.utility.FileUtils
import co.deshbidesh.db_android.shared.utility.FileUtilsImpl
import java.text.SimpleDateFormat
import java.util.*


class DBDocScanSaveFragment : Fragment() {

    companion object{

        const val TAG = "DBDocScanSaveFragment"

        val writeExternalStoragePermissions = arrayOf(DBPermissionConstants.WriteExternalStorage)
    }

    private var _binding: FragmentDbDocScanSaveBinding? = null

    private val binding get() = _binding!!

    private val args: DBDocScanSaveFragmentArgs by navArgs()

    private lateinit var saveImageObject: DBDocScanSaveObject

    private val fileUtils: FileUtils by lazy { FileUtilsImpl(requireActivity()) }

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val sharedNoteDetailViewModel: DBNoteDetailViewModel by activityViewModels { DBNoteDetailViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbDocScanSaveBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dbDocScanSaveFragmentToolbar.setOnMenuItemClickListener {  item ->

            when(item.itemId) {

                R.id.doc_scan_save_fragment_menu -> {

                    askWritePermission()
                    true
                }
                else -> false
            }
        }

        args.dbSaveObject?.let {

            saveImageObject = it

            sharedViewModel.setRoute(it.route)

            binding.dbDocScanSaveFragmentImageView.setImageBitmap(it.bitmap)
        } ?: run {

            // we sud not be in this fragment when there is no bitmap to save
            findNavController().popBackStack()
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun saveNote(){

        val title = binding.dbDocScanSaveFragmentEditText.text.toString()

        val content = binding.dbDocScanSaveFragmentMultiLineEditText.text.toString()

        if (title.isNotBlank()) {

            sharedNoteDetailViewModel.addNote(title, content) { optionalId ->

                optionalId?.let { id ->

                    sharedNoteDetailViewModel.getNote(id) { note ->

                        val path = saveImageToFile(saveImageObject.bitmap)

                        sharedNoteDetailViewModel.addImage(note.id, path) {

                            activity?.let{

                                it.finish()
                            }
                        }
                    }
                }
            }

            hideKeyboard()

        } else {
            showToast("Please enter title.")
        }
    }

    private fun showToast(message: String) {

        activity?.runOnUiThread {

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

    }

    private fun saveImageToFile(bitmap: Bitmap): String {

        val dir  = fileUtils.createDirectoryIfNotExist()

        val file = fileUtils.makeFile(
                dir,
                SimpleDateFormat(
                    NotesImageUtils.FILENAME_FORMAT,
                        Locale.UK
                ).format(
                        System.currentTimeMillis()
                ) + ".jpg"
        )

        fileUtils.writeImageToExternalStorage(file, bitmap)

        fileUtils.refreshGallery(file)

        return file.path
    }


    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            DBPermissionConstants.writeExternalStoragePermissionCode -> {

                if (permission(grantResults)) {

                    saveNote()

                } else {

                    showToast("Please grant permission to save image.")
                }

            }
        }
    }

    private fun permission(grantResults: IntArray): Boolean {

        return if(grantResults.isNotEmpty()) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                true

            } else {

                showAlert("DeshBidesh App will require permission to work")

                false
            }
        } else {

            showAlert("Something went wrong. Please try again later")

            false
        }
    }

    private fun askWritePermission() {

        if (activity?.hasPermission(*writeExternalStoragePermissions) == true) {

            saveNote()

        } else {

            requestPermissions(
                    writeExternalStoragePermissions,
                    DBPermissionConstants.writeExternalStoragePermissionCode
            )
        }
    }
}