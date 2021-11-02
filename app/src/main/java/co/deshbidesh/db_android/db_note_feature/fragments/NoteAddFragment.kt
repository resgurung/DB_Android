package co.deshbidesh.db_android.db_note_feature.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteAddBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_note_feature.repository.DBImageRepository
import co.deshbidesh.db_android.db_note_feature.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteAddImageRecyclerAdapter
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteAddViewModelFactory
import co.deshbidesh.db_android.db_note_feature.note_utils.NotesImageUtils
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteAddViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.hideKeyboard
import co.deshbidesh.db_android.shared.utility.DBPermissionConstants
import co.deshbidesh.db_android.shared.utility.FileUtils
import co.deshbidesh.db_android.shared.utility.FileUtilsImpl
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.IOException


class NoteAddFragment : DBBaseFragment() {

    private lateinit var toolbar: Toolbar

    private lateinit var bottomNavBar: BottomNavigationView

    override var bottomNavigationViewVisibility = View.GONE

    private var binding: FragmentNoteAddBinding? = null

    private lateinit var addViewModel: DBNoteAddViewModel

    private lateinit var addViewModelFactory: DBNoteAddViewModelFactory

    private lateinit var noteAddImgAdapter: DBNoteAddImageRecyclerAdapter

    // Track imagePaths to be displayed in the recycle view list
    private var displayImgList: ArrayList<String> = ArrayList()

    // Track imagePaths selected from Gallery
    private var uriList: ArrayList<String> = ArrayList()

    // Track imagePaths taken from Camera
    private var cameraImgList: ArrayList<String> = ArrayList()

    // List of final imagePaths to be written to database
    private var destFilePath:ArrayList<String> =  ArrayList()

    // Map of bitmap and file selected from Gallery
    private var bitMapFileMap: HashMap<Bitmap, File> = HashMap()

     // Current imageUri of camera picture
     private lateinit var currentPhotoUri: Uri

    private val fileUtils: FileUtils by lazy { FileUtilsImpl(requireActivity()) }

    // Static fields
    companion object{
        private const val IMAGE_PICK_CODE = 1000
        private const val IMAGE_CAPTURE_CODE = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNoteAddBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Init recyclerView and set adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        noteAddImgAdapter = DBNoteAddImageRecyclerAdapter()
        binding?.addNoteRecyclerView?.layoutManager = layoutManager
        binding?.addNoteRecyclerView?.adapter = noteAddImgAdapter

        // Init toolbar
        toolbar = binding!!.noteAddToolbar
        toolbar.inflateMenu(R.menu.note_save)
        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){

                R.id.save_note_db -> {
                    saveNote()
                    true
                }
                else -> false
            }
        }

        // Init bottom nav bar
        bottomNavBar = binding!!.noteAddBottomNav
        bottomNavBar.setOnNavigationItemSelectedListener { item ->

            when(item.itemId){

                R.id.openGallery ->{
                    if(askStoragePermission()){
                        pickImage()
                    }
                    true
                }

                R.id.openCamera->{
                    if(askCameraPermission()){
                        takePicture()
                    }
                    true
                }

                else -> false
            }
        }

        // Focus on note content edit text text upon this fragment call
        binding!!.noteAddContentEdittext.requestFocus()
        val inputMethodManager: InputMethodManager =
            this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        inputMethodManager.showSoftInput(binding!!.noteAddContentEdittext, InputMethodManager.SHOW_IMPLICIT);

        //
        context.let {

            binding?.noteAddToolbar?.setNavigationOnClickListener {

                // Delete the unsaved camera images
                requireActivity().onBackPressed().apply {
                    if(cameraImgList.isNotEmpty()){
                        NotesImageUtils.deleteImagesFromStorage(cameraImgList);
                    }
                }
            }

            // Init required ViewModel Factories
            addViewModelFactory = DBNoteAddViewModelFactory(
                DBNoteRepository(DBDatabase.getDatabase(requireContext()).noteDAO()),
                DBImageRepository(DBDatabase.getDatabase(requireContext()).imageDAO()),
                DBHelper())

            addViewModel = ViewModelProvider(this, addViewModelFactory).get(DBNoteAddViewModel::class.java)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onResume() {
        super.onResume()

        noteAddImgAdapter.setData(displayImgList)  // re-load image paths
    }

    // Save note
    private fun saveNote(){
        addViewModel.title = binding?.noteAddTitleEdittext?.text.toString()

        addViewModel.content = binding?.noteAddContentEdittext?.text.toString()

        if (!addViewModel.isTextEmpty()) {

            addViewModel.addNote { id -> // thread 1, addNote returns NoteId

                if (id != null) {

                    NotesImageUtils.prepareFilePath(
                        uriList,
                        destFilePath,
                        bitMapFileMap,
                        requireContext(),
                        fileUtils)

                    // Add camera images to destFilePath
                    if(cameraImgList.isNotEmpty()){
                        destFilePath.addAll(cameraImgList)
                    }

                    addViewModel.addImage(destFilePath, id) { imageList -> //thread 2, addImages returns imageIdList

                        if (imageList.isNotEmpty()) {

                            addViewModel.getNote(id.toLong()) { currNote -> // thread 3, getNote again, set the imageIds

                                currNote.imageIds = imageList

                                addViewModel.updateNote(currNote) { // thread 4, finally update Note with imageIds

                                    NotesImageUtils
                                        .writeImagesToExternalStorage(bitMapFileMap)

                                    createNoteFinalize("Note saved")
                                }
                            }

                        } else {

                            createNoteFinalize("Note saved")
                        }
                    }

                } else {

                    createNoteFinalize("Something went wrong. Please try again")
                }
            }

            hideKeyboard()

        } else {

            showToast("Fields cannot be empty")
        }
    }

    private fun createNoteFinalize(msg: String) {
        activity?.runOnUiThread {
            uriList.clear()
            displayImgList.clear()
            destFilePath.clear()
            cameraImgList.clear()
            bitMapFileMap.clear()
            showToast(msg)
            findNavController().navigate(R.id.action_noteAddFragment_to_noteListFragment)
        }
    }

    // Upload Image
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Take camera picture
    private fun takePicture(){

        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (photoIntent.resolveActivity(
                requireActivity().packageManager
            ) != null){

            var photoFile: File? = try {

                 NotesImageUtils.createImageFile(
                     requireActivity(),
                     cameraImgList)

            }catch (ex:IOException){
                return
            }

            photoFile?.also {

                currentPhotoUri = FileProvider
                    .getUriForFile(requireContext(),
                    "co.deshbidesh.db_android.provider",
                    it)

                photoIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    currentPhotoUri
                )
                startActivityForResult(photoIntent, IMAGE_CAPTURE_CODE )
            }
        }
    }

    // Handle image pick result (Set image and get thumbnail)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?) {

        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_PICK_CODE)) {

            uriList.add(data?.data.toString())

            displayImgList.add(data?.data.toString())

            noteAddImgAdapter.setData(displayImgList)
        }

        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_CAPTURE_CODE)) {

            displayImgList.add(currentPhotoUri.toString())

            noteAddImgAdapter.setData(displayImgList)
        }
    }

    // Storage permission
    private fun askStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                DBPermissionConstants.ReadExternalStorage
            ) == PackageManager.PERMISSION_GRANTED){
            return true

        } else {
            val permissions = arrayOf(DBPermissionConstants.ReadExternalStorage)
            requestPermissions(
                permissions,
                DBPermissionConstants.readExternalStoragePermissionCode
            )
        }

        return false
    }


    // Camera permission
    private fun askCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                DBPermissionConstants.CameraPermission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true

        } else {

            val permissions = arrayOf(DBPermissionConstants.CameraPermission)
            requestPermissions(
                permissions,
                DBPermissionConstants.cameraPermissionCode)
        }

        return false
    }
}