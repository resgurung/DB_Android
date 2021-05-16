package co.deshbidesh.db_android.db_note_feature.fragments

import android.app.Activity
import android.app.AlertDialog
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
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteDetailBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBImageRepository
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteDetailImageRecyclerAdapter
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.note_utils.NotesImageUtils
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.hideKeyboard
import co.deshbidesh.db_android.shared.utility.DBPermissionConstant
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.IOException


class NoteDetailFragment : DBBaseFragment(), DBNoteDetailImageRecyclerAdapter.InterfaceDeleteImage {

    private var binding: FragmentNoteDetailBinding? = null

    private lateinit var bottomNavBar: BottomNavigationView

    private lateinit var toolbar: Toolbar

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteDetailFragmentArgs>()

    private lateinit var noteDetailViewModel: DBNoteDetailViewModel

    private lateinit var noteDetailViewModelFactory: DBNoteDetailViewModelFactory

    private lateinit var noteDetailImageAdapter: DBNoteDetailImageRecyclerAdapter

    // Determine first call to database
    private var isFirstFlag: Boolean = true

    // Track recycler view images
    private var displayImgList: ArrayList<String> = arrayListOf()

    // Track original array of images associated with the current note
    private var originalImagesList: ArrayList<String> = arrayListOf()

    // Track original images to be deleted
    private var imagesToBeDeleted: ArrayList<String> = arrayListOf()

    // Track new images to be added to the database for the current note
    private var newImagesList: ArrayList<String> = arrayListOf()

    // Store final path of newly added images
    private var destFilePath:ArrayList<String> =  arrayListOf()

    // Store new image bitmap of newly added images to be written to eternal storage
    private var bitMapFileMap: HashMap<Bitmap, File> = hashMapOf()

    // Track imagePaths taken from Camera
    private var cameraImgList: ArrayList<String> = ArrayList()

    // Current imageUri of camera picture
    private lateinit var currentPhotoUri: Uri


    // Static fields
    companion object{
        private const val IMAGE_PICK_CODE = 1000
        private const val IMAGE_CAPTURE_CODE = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set image adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        noteDetailImageAdapter = DBNoteDetailImageRecyclerAdapter(this)
        binding?.detailNoteRecyclerView?.layoutManager = layoutManager
        binding?.detailNoteRecyclerView?.adapter = noteDetailImageAdapter


        // Init view models
        noteDetailViewModelFactory = DBNoteDetailViewModelFactory(
            DBNoteRepository(DBDatabase.getDatabase(requireContext()).noteDAO()),
            DBImageRepository(DBDatabase.getDatabase(requireContext()).imageDAO()),
            DBHelper(), args.noteDetail)

        noteDetailViewModel = ViewModelProvider(this, noteDetailViewModelFactory).get(DBNoteDetailViewModel::class.java)

        // Bind UI views
        binding?.noteDetailTitle?.setText(noteDetailViewModel.getNote().title)      // editText expects Editable

        binding?.noteDetailContent?.setText(noteDetailViewModel.getNote().content)

        binding?.noteDetailToolbar?.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        toolbar = binding!!.noteDetailToolbar

        toolbar.inflateMenu(R.menu.db_note_detail_toolbar_menu)

        // predefined: true -> saveNote(only if the condition is true)
        val result = true

        // Bind toolbar menu options
        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){

                R.id.deleteNote -> {

                    showDeleteDialog(requireContext())

                    true
                }

                R.id.saveNote -> {

                    when(result){

                        (newImagesList.isNotEmpty() && cameraImgList.isNotEmpty() && imagesToBeDeleted.isNotEmpty()) -> {
                            NotesImageUtils.prepareFilePath(newImagesList, destFilePath, bitMapFileMap, requireContext(),requireActivity())

                            destFilePath.addAll(cameraImgList)

                            noteDetailViewModel.addImage(destFilePath) { newImgIdList ->  // add new images to database

                                noteDetailViewModel.getImageIdByPath(imagesToBeDeleted) { imageIds ->

                                    if (imageIds.isNotEmpty()) {

                                        noteDetailViewModel.deleteSingleImageById(imageIds){

                                            noteDetailViewModel.updatedImageIds.removeAll(imageIds)  // remove the deleted images ids

                                            NotesImageUtils.deleteImagesFromStorage(imagesToBeDeleted);

                                            noteDetailViewModel.updatedImageIds.addAll(newImgIdList) // add new image ids

                                            upDateNote()
                                        }
                                    }
                                }
                            }
                        }

                        (newImagesList.isNotEmpty() && imagesToBeDeleted.isNotEmpty()) -> {
                            NotesImageUtils.prepareFilePath(newImagesList, destFilePath, bitMapFileMap, requireContext(),requireActivity())

                            noteDetailViewModel.addImage(destFilePath) { newImgIdList ->  // add new images to database

                                noteDetailViewModel.getImageIdByPath(imagesToBeDeleted) { imageIds ->

                                    if (imageIds.isNotEmpty()) {

                                        noteDetailViewModel.deleteSingleImageById(imageIds){

                                            noteDetailViewModel.updatedImageIds.removeAll(imageIds)  // remove the deleted images ids

                                            NotesImageUtils.deleteImagesFromStorage(imagesToBeDeleted);

                                            noteDetailViewModel.updatedImageIds.addAll(newImgIdList) // add new image ids

                                            upDateNote()
                                        }
                                    }
                                }
                            }
                        }

                        (cameraImgList.isNotEmpty() && imagesToBeDeleted.isNotEmpty()) -> {

                            noteDetailViewModel.addImage(cameraImgList) { newImgIdList ->  // add new images to database

                                noteDetailViewModel.getImageIdByPath(imagesToBeDeleted) { imageIds ->

                                    if (imageIds.isNotEmpty()) {

                                        noteDetailViewModel.deleteSingleImageById(imageIds){

                                            noteDetailViewModel.updatedImageIds.removeAll(imageIds)  // remove the deleted images ids

                                            NotesImageUtils.deleteImagesFromStorage(imagesToBeDeleted);

                                            noteDetailViewModel.updatedImageIds.addAll(newImgIdList) // add new image ids

                                            upDateNote()
                                        }
                                    }
                                }
                            }
                        }

                        (newImagesList.isNotEmpty() && cameraImgList.isNotEmpty()) -> {
                            NotesImageUtils.prepareFilePath(newImagesList, destFilePath, bitMapFileMap, requireContext(),requireActivity())

                            destFilePath.addAll(cameraImgList)

                            noteDetailViewModel.addImage(destFilePath) { newImgIdList ->  // add new images to database

                                noteDetailViewModel.updatedImageIds.addAll(newImgIdList)
                                upDateNote()
                            }
                        }

                        (newImagesList.isNotEmpty()) -> {
                            NotesImageUtils.prepareFilePath(newImagesList, destFilePath, bitMapFileMap, requireContext(),requireActivity())

                            noteDetailViewModel.addImage(destFilePath) { newImgIdList ->  // add new images to database

                                    noteDetailViewModel.updatedImageIds.addAll(newImgIdList)
                                    upDateNote()
                            }
                        }

                        (cameraImgList.isNotEmpty()) ->{
                            noteDetailViewModel.addImage(cameraImgList) { newImgIdList ->  // add new images to database

                                noteDetailViewModel.updatedImageIds.addAll(newImgIdList)
                                upDateNote()
                            }
                        }

                        (imagesToBeDeleted.isNotEmpty()) -> {
                            deleteImage()
                        }

                        else -> upDateNote()
                    }

                    true
                }

                else -> false
            }
        }

        // Bind Bottom nav bar options
        bottomNavBar = binding!!.noteDetailBottomNav
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

        // Only make call to room database for the first time
        if(isFirstFlag){
            getImageList()
        }else{
            setImageAdapter()
        }
    }



    // Get Notes image list from database
    private fun getImageList(){

        noteDetailViewModel.getImageListByNoteId {

            if(it.isNotEmpty()){
                for(img in it){

                    val path = img.imgPath

                    displayImgList.add(path)
                }

                originalImagesList.addAll(displayImgList)

                isFirstFlag = false

                // UI Thread
                setImageAdapter()
            }
        }

        // Keep reference of imageIDs
        noteDetailViewModel.getNote().imageIds?.let { noteDetailViewModel.updatedImageIds.addAll(it) }
    }


    private fun setImageAdapter(){
        // UI Thread
        activity?.runOnUiThread {

            noteDetailImageAdapter.setData(displayImgList)
        }
    }


    // Update note upon save button click
    private fun upDateNote(){
        noteDetailViewModel.title = binding?.noteDetailTitle?.text.toString()

        noteDetailViewModel.content = binding?.noteDetailContent?.text.toString()

        if (!noteDetailViewModel.isTextEmpty()) {

            if(!noteDetailViewModel.isSame() || newImagesList.isNotEmpty() || imagesToBeDeleted.isNotEmpty()){

                noteDetailViewModel.updateNote(){

                    if(bitMapFileMap.isNotEmpty()){

                        NotesImageUtils.writeImagesToExternalStorage(bitMapFileMap)
                    }
                }
            }

            finalizeSaveNote("Note updated")

            activity?.runOnUiThread {
                findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
            }

        } else {

            finalizeSaveNote("Fields cannot be empty")
        }
    }


    private fun finalizeSaveNote(message: String){
        activity?.runOnUiThread {

            hideKeyboard()

            showToast(message)
        }
    }


    // Delete entire note upon delete icon click
    private fun showDeleteDialog(context: Context) {

        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete the note?")
            .setPositiveButton("Yes"){
                    _, _ ->
                // Delete here
                noteDetailViewModel.deleteNote {     // delete note from database

                    if(originalImagesList.isNotEmpty()) {

                        noteDetailViewModel.deleteImages(){     // delete images data from database if any

                            NotesImageUtils.deleteImagesFromStorage(originalImagesList) // finally delete image from storage

                            showToast("Note deleted")

                            findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
                        }
                    } else {

                        showToast("Note deleted")

                        findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
                    }
                }
            }

            .setNegativeButton("Cancel") {

                    dialog, _ -> dialog.dismiss()

            }.show()
    }


    // Delete Image on update button click
    private fun deleteImage(){

        noteDetailViewModel.getImageIdByPath(imagesToBeDeleted) { imageIds ->

            if (imageIds.isNotEmpty()) {

                noteDetailViewModel.deleteSingleImageById(imageIds){

                    noteDetailViewModel.updatedImageIds.removeAll(imageIds) // remove the deleted images ids

                    NotesImageUtils.deleteImagesFromStorage(imagesToBeDeleted)

                    upDateNote()
                }
            }
        }
    }

    // Pick image from gallery
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Take camera picture
    private fun takePicture(){

        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (photoIntent.resolveActivity(requireActivity().packageManager) != null){

            var photoFile: File? = try {

                NotesImageUtils.createImageFile(requireActivity(), cameraImgList)

            }catch (ex: IOException){
                return
            }

            photoFile?.also {
                currentPhotoUri = FileProvider.getUriForFile(requireContext(),
                    "co.deshbidesh.db_android.provider",
                    it)

                photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
                startActivityForResult(photoIntent, IMAGE_CAPTURE_CODE)

            }
        }
    }

    // Handle image pick result (Set image and get thumbnail)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_PICK_CODE)) {

            displayImgList.add(data?.data.toString())   // update recycler view when image added

            newImagesList.add(data?.data.toString())   // update list of images to be added to database

            setImageAdapter()
        }

        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_CAPTURE_CODE)) {

            displayImgList.add(currentPhotoUri.toString())

            noteDetailImageAdapter.setData(displayImgList)
        }
    }

    // Handle delete image alert dialog positive button
    override fun handleDeleteImage(imgPath: String) {

        if(originalImagesList.contains(imgPath)){

            imagesToBeDeleted.add(imgPath)
        }

        displayImgList.remove(imgPath)       // for recycler view

        newImagesList.remove(imgPath)       // for new image added

        setImageAdapter()
    }

    // Storage permission
    private fun askStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), DBPermissionConstant.ReadExternalStorage )
            == PackageManager.PERMISSION_GRANTED){
            return true

        } else {
            val permissions = arrayOf(DBPermissionConstant.ReadExternalStorage)
            requestPermissions(permissions, DBPermissionConstant.readExternalStoragePermissionCode)
        }

        return false
    }


    // Camera permission
    private fun askCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), DBPermissionConstant.CameraPermission)
            == PackageManager.PERMISSION_GRANTED) {
            return true

        } else {

            val permissions = arrayOf(DBPermissionConstant.CameraPermission)
            requestPermissions(permissions, DBPermissionConstant.cameraPermissionCode)
        }

        return false
    }
}
