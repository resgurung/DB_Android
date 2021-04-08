package co.deshbidesh.db_android.db_note_feature.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
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
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.hideKeyboard
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class NoteDetailFragment : DBBaseFragment(), DBNoteDetailImageRecyclerAdapter.InterfaceDeleteImage {

    private var binding: FragmentNoteDetailBinding? = null

    private lateinit var bottomNavBar: BottomNavigationView

    private lateinit var toolbar: Toolbar

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteDetailFragmentArgs>()

    private lateinit var noteDetailViewModel: DBNoteDetailViewModel

    private lateinit var noteDetailViewModelFactory: DBNoteDetailViewModelFactory

    private lateinit var noteDetailImageAdapter: DBNoteDetailImageRecyclerAdapter

    // To determine first call to database
    private var isFirstFlag: Boolean = true

    // To track recycler view images
    private var imagePathList: ArrayList<String> = arrayListOf()

    // To track original array of images associated with the current note
    private var originalList: ArrayList<String> = arrayListOf()

    // To track original images to be deleted
    private var imagesToBeDeleted: ArrayList<String> = arrayListOf()

    // To track new images to be added to the database for the current note
    private var newList: ArrayList<String> = arrayListOf()

    // To store final path of newly added images
    private var destFilePath:ArrayList<String> =  arrayListOf()

    // To store new image bitmap of newly added images to be written to eternal storage
    private var bitMapFileMap: HashMap<Bitmap, File> = hashMapOf()


    // Static fields
    companion object{
        private const val IMAGE_PICK_CODE = 1000
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

        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){

                R.id.deleteNote -> {

                    showDeleteDialog(requireContext())

                    true
                }

                R.id.saveNote -> {

                    if(imagesToBeDeleted.isNotEmpty() || newList.isNotEmpty()) {

                        if(newList.isNotEmpty()) {

                            prepareFilePath(newList)

                            noteDetailViewModel.addImage(destFilePath) { newImgIdList ->  // add new images to database

                                // 4th condition
                                if(imagesToBeDeleted.isNotEmpty()){
                                    noteDetailViewModel.getImageIdByPath(imagesToBeDeleted) { imageIds ->

                                        println("imgIds $imageIds")

                                        if (imageIds.isNotEmpty()) {

                                            noteDetailViewModel.deleteSingleImageById(imageIds){

                                                noteDetailViewModel.updatedImageIds.removeAll(imageIds) // remove the deleted images ids

                                                deleteImagesFromStorage(imagesToBeDeleted)

                                                noteDetailViewModel.updatedImageIds.addAll(newImgIdList)

                                                upDateNote()
                                            }
                                        }
                                    }

                                 // 3rd condition
                                } else {

                                    noteDetailViewModel.updatedImageIds.addAll(newImgIdList)
                                    upDateNote()
                                }
                            }

                        // 2nd condition
                        }else{

                            deleteImage()
                        }

                    // 1st condition
                    }else{

                        upDateNote()
                    }

                    true
                }

                else -> false
            }
        }


        bottomNavBar = binding!!.noteDetailBottomNav
        bottomNavBar.setOnNavigationItemSelectedListener { item ->

            when(item.itemId){

                R.id.openGallery ->{

                    pickImage()

                    true
                }

                R.id.openCamera->{

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

    // Delete images from storage
    private fun deleteImagesFromStorage(list: ArrayList<String>) {

        for(path in list) {
            val file = File(path)
            file.delete();
        }
    }

    private fun getImageList(){

        noteDetailViewModel.getImageListByNoteId {

            if(it.isNotEmpty()){
                for(img in it){

                    val path = img.imgPath

                    imagePathList.add(path)
                }

                originalList.addAll(imagePathList)

                isFirstFlag = false

                // UI Thread
                setImageAdapter()
            }
        }

        noteDetailViewModel.getNote().imageIds?.let { noteDetailViewModel.updatedImageIds.addAll(it) }
    }


    private fun setImageAdapter(){
        // UI Thread
        activity?.runOnUiThread {

            noteDetailImageAdapter.setData(imagePathList)
        }
    }


    // Update note upon save button click
    private fun upDateNote(){
        noteDetailViewModel.title = binding?.noteDetailTitle?.text.toString()

        noteDetailViewModel.content = binding?.noteDetailContent?.text.toString()

        if (!noteDetailViewModel.isTextEmpty()) {

            if(!noteDetailViewModel.isSame() || newList.isNotEmpty() || imagesToBeDeleted.isNotEmpty()){

                noteDetailViewModel.updateNote(){

                    println("imgBitmap $bitMapFileMap")
                    if(bitMapFileMap.isNotEmpty()){

                        writeImagesToExternalStorage(bitMapFileMap)
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
                // delete here
                noteDetailViewModel.deleteNote {     // delete note from database

                    if(originalList.isNotEmpty()) {

                        noteDetailViewModel.deleteImages(){     // delete images data from database if any

                            deleteImagesFromStorage(originalList)     // finally delete image from storage

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

                    deleteImagesFromStorage(imagesToBeDeleted)

                    upDateNote()
                }
            }
        }
    }

    // Upload Image
    private fun pickImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // Handle image pick result (Set image and get thumbnail)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_PICK_CODE)) {

            imagePathList.add(data?.data.toString())   // update recycler view when image added

            newList.add(data?.data.toString())      // update list of images to be added to database

            setImageAdapter()
        }
    }

    // Handle delete image alert dialog positive button
    override fun handleDeleteImage(imgPath: String) {

        if(originalList.contains(imgPath)){

            imagesToBeDeleted.add(imgPath)
        }

        imagePathList.remove(imgPath)       // for recycler view

        newList.remove(imgPath)             // for new image added

        setImageAdapter()
    }

    private fun prepareFilePath(imageList: ArrayList<String>){
        for (path in imageList) {
            // Decode bitmap from uri
            val imgUri: Uri? = Uri.parse(path)
            val imgStream: InputStream? = context?.contentResolver?.openInputStream(imgUri!!)

            imgStream?.let {
                val selectedImg: Bitmap = BitmapFactory.decodeStream(imgStream)

                // Determine destination file path
                val dirPath = this.requireActivity().getExternalFilesDir("/images/")?.absolutePath
                val file: File = File(dirPath, "${System.currentTimeMillis()}.jpg")
                val finalFilePath = file.path
                destFilePath.add(finalFilePath)
                bitMapFileMap.put(selectedImg, file)
            }
        }
    }


    private fun writeImagesToExternalStorage(fileMap: HashMap<Bitmap, File>) {
        var outputStream: OutputStream?
        fileMap.forEach {
            outputStream = FileOutputStream(it.value)
            it.key.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    }

}
