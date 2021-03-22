package co.deshbidesh.db_android.db_note_feature.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteAddBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_database.repository.DBImageRepository
import co.deshbidesh.db_android.db_database.repository.DBNoteRepository
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteAddImageRecyclerAdapter
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteAddViewModelFactory
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteAddViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.hideKeyboard
import kotlinx.android.synthetic.main.fragment_note_add.*
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class NoteAddFragment : DBBaseFragment() {

    private lateinit var toolbar: Toolbar

    override var bottomNavigationViewVisibility = View.GONE

    private var binding: FragmentNoteAddBinding? = null

    private lateinit var addViewModel: DBNoteAddViewModel

    private lateinit var addViewModelFactory: DBNoteAddViewModelFactory

    private lateinit var noteAddImgAdapter: DBNoteAddImageRecyclerAdapter

    private var uriList: ArrayList<String> = ArrayList()

    private var destFilePath:ArrayList<String> =  ArrayList()

    private var bitMapFileMap: HashMap<Bitmap, File> = HashMap()

    // Static fields
    companion object{
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
        private const val STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE
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

        // Initialize recyclerView and set adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        noteAddImgAdapter = DBNoteAddImageRecyclerAdapter()
        binding?.addNoteRecyclerView?.layoutManager = layoutManager
        binding?.addNoteRecyclerView?.adapter = noteAddImgAdapter

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

        context.let {

            binding?.noteAddToolbar?.setNavigationOnClickListener {

                hideKeyboard()

                requireActivity().onBackPressed()
            }


            addViewModelFactory = DBNoteAddViewModelFactory(
                DBNoteRepository(DBDatabase.getDatabase(requireContext()).noteDAO()),
                DBImageRepository(DBDatabase.getDatabase(requireContext()).imageDAO()),
                DBHelper())

            addViewModel = ViewModelProvider(this, addViewModelFactory).get(DBNoteAddViewModel::class.java)

            binding?.addNoteFab?.setOnClickListener{
                if(checkPermission()){
                    pickImage()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


    private fun saveNote(){
        addViewModel.title = binding?.noteAddTitleEdittext?.text.toString()

        addViewModel.content = binding?.noteAddContentEdittext?.text.toString()

        if (!addViewModel.isTextEmpty()) {

            addViewModel.addNote { id -> // thread 1, addNote returns NoteId

                if (id != null) {

                    prepareFilePath(uriList)

                    addViewModel.addImage(destFilePath, id) { imageList -> //thread 2, addImages returns imageIdList

                        if (imageList.isNotEmpty()) {

                            addViewModel.getNote(id.toLong()) { currNote -> // thread 3, getNote again, set the imageIds

                                currNote.imageIds = imageList

                                addViewModel.updateNote(currNote) { // thread 4, finally update Note with imageIds

                                    writeImagesToExternalStorage(bitMapFileMap);

                                    goBack("Successful, note saved.")
                                }
                            }

                        } else {

                            goBack("msg")
                        }
                    }

                } else {

                    goBack("Something went wrong..")
                }
            }

            hideKeyboard()

        } else {

            showToast("Fields cannot be empty")
        }
    }


    private fun goBack(msg: String) {

        activity?.runOnUiThread {
            uriList.clear();
            destFilePath.clear();
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

    // Handle image pick result (Set image and get thumbnail)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_PICK_CODE)) {
            uriList.add(data?.data.toString())
            noteAddImgAdapter.setData(uriList)
        }
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

    // Validate user permission
    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireContext(), STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED){
            return true
        } else {
            val permissions = arrayOf(STORAGE_PERMISSION)
            requestPermissions(permissions, PERMISSION_CODE)
        }
        return false
    }
}