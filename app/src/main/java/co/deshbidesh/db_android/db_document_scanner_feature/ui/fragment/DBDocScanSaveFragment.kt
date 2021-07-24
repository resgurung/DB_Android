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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbDocScanSaveBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBImageRepository
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanSaveObject
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.SharedViewModel
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteAddViewModelFactory
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteAddViewModel
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.extensions.hasPermission
import co.deshbidesh.db_android.shared.extensions.showAlert
import co.deshbidesh.db_android.shared.hideKeyboard
import co.deshbidesh.db_android.shared.utility.DBPermissionConstants
import co.deshbidesh.db_android.shared.utility.FileUtils
import co.deshbidesh.db_android.shared.utility.FileUtilsImpl
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DBDocScanSaveFragment : Fragment() {

    companion object{

        const val TAG = "DBDocScanSaveFragment"

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        val writeExternalStoragePermissions = arrayOf(DBPermissionConstants.WriteExternalStorage)
    }

    private var _binding: FragmentDbDocScanSaveBinding? = null

    private val binding get() = _binding!!

    val args: DBDocScanSaveFragmentArgs by navArgs()

    private lateinit var addViewModel: DBNoteAddViewModel

    private lateinit var addViewModelFactory: DBNoteAddViewModelFactory

    private lateinit var saveImageObject: DBDocScanSaveObject

    private val fileUtils: FileUtils by lazy { FileUtilsImpl(requireActivity()) }

    private val sharedViewModel: SharedViewModel by activityViewModels()

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

        addViewModelFactory = DBNoteAddViewModelFactory(
                DBNoteRepository(
                        DBDatabase.getDatabase(requireContext()).noteDAO()
                ),
                DBImageRepository(
                        DBDatabase.getDatabase(requireContext()).imageDAO()
                ),
                DBHelper())

        addViewModel = ViewModelProvider(
                this,
                addViewModelFactory).get(DBNoteAddViewModel::class.java
        )

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

        addViewModel.title = binding.dbDocScanSaveFragmentEditText.text.toString()

        addViewModel.content = binding.dbDocScanSaveFragmentMultiLineEditText.text.toString()

        if (!addViewModel.isTextEmpty()) {

            addViewModel.addNote { id -> // thread 1, addNote returns NoteId

                if (id != null) {

                    val path = saveImageToFile(saveImageObject.bitmap)

                    var arraylist = ArrayList<String>()

                    arraylist.add(path)

                    addViewModel.addImage(arraylist, id) { imageList -> //thread 2, addImages returns imageIdList

                        if (imageList.isNotEmpty()) {

                            addViewModel.getNote(id.toLong()) { currNote -> // thread 3, getNote again, set the imageIds

                                currNote.imageIds = imageList

                                addViewModel.updateNote(currNote) { // thread 4, finally update Note with imageIds

                                    showToast("Successful, note saved.")

                                    activity?.let{

                                        it.finish()
                                    }
                                }
                            }

                        } else {

                            showToast("Image List is empty")
                        }
                    }

                } else {

                    showToast("Note was not saved. Please try again")
                }
            }

            hideKeyboard()

        } else {

            showToast("Fields cannot be empty")
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
                        FILENAME_FORMAT,
                        Locale.UK
                ).format(
                        System.currentTimeMillis()
                ) + ".jpg"
        )

        writeImagesToExternalStorage(file, bitmap)

        fileUtils.refreshGallery(file)

        return file.path
    }

    private fun writeImagesToExternalStorage(file: File, bitmap: Bitmap) {

        val outputStream: OutputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) // 90 is optimal

        outputStream.flush()

        outputStream.close()
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