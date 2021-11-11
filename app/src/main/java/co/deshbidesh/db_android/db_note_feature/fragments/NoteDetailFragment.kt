package co.deshbidesh.db_android.db_note_feature.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentNoteDetailBinding
import co.deshbidesh.db_android.db_note_feature.adapters.DBNoteImageListAdapter
import co.deshbidesh.db_android.db_note_feature.factories.DBNoteDetailViewModelFactory
import co.deshbidesh.db_android.db_note_feature.models.DBImage
import co.deshbidesh.db_android.db_note_feature.models.DBNoteUIImage
import co.deshbidesh.db_android.db_note_feature.note_utils.NotesImageUtils
import co.deshbidesh.db_android.db_note_feature.viewmodel.DBNoteDetailViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.hideKeyboard
import co.deshbidesh.db_android.shared.utility.DBPermissionConstants
import co.deshbidesh.db_android.shared.utility.FileUtils
import co.deshbidesh.db_android.shared.utility.FileUtilsImpl
import com.google.android.material.bottomnavigation.BottomNavigationView


class NoteDetailFragment :
    DBBaseFragment(),
    DBNoteImageListAdapter.DBNoteImageInterface {

    private var _binding: FragmentNoteDetailBinding? = null

    private val binding get() = _binding!!

    private lateinit var bottomNavBar: BottomNavigationView

    private lateinit var toolbar: Toolbar

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteDetailFragmentArgs>()

    private lateinit var noteImageListAdapter: DBNoteImageListAdapter

    private val fileUtils: FileUtils by lazy { FileUtilsImpl(requireActivity()) }

    private val sharedNoteDetailViewModel: DBNoteDetailViewModel by activityViewModels { DBNoteDetailViewModelFactory }

    // Static fields
    companion object{

        private const val IMAGE_PICK_CODE = 1000

        private const val IMAGE_CAPTURE_CODE = 1002
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInitial()

        setupToolbar()

        setupBottomNav()

        setupListview()
    }

    private fun setupInitial() {

        sharedNoteDetailViewModel.fileUtils = fileUtils

        // Bind UI views
        binding.noteDetailTitle.setText(args.noteDetail.title)      // editText expects Editable

        binding.noteDetailContent.setText(args.noteDetail.content)
    }

    private fun setupListview() {

        // Set image adapter
        val layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)

        noteImageListAdapter = DBNoteImageListAdapter(this)

        binding.detailNoteRecyclerView.layoutManager = layoutManager

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
        binding.detailNoteRecyclerView.addItemDecoration(decoration)

        binding.detailNoteRecyclerView.adapter = noteImageListAdapter//noteDetailImageAdapter

        sharedNoteDetailViewModel.images.observe(viewLifecycleOwner, {
            noteImageListAdapter.submitList(it.toMutableList())
        })

        sharedNoteDetailViewModel.getImagesFromDatabase(args.noteDetail.id)
    }

    private fun setupToolbar() {
        // set the title of the page
        binding.noteDetailToolbarTitle.text = args.noteDetail.title

        toolbar = binding.noteDetailToolbar

        toolbar.inflateMenu(R.menu.db_note_detail_toolbar_menu)

        toolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        // Bind toolbar menu options
        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){

                R.id.deleteNote -> {

                    sharedNoteDetailViewModel.deleteNote(args.noteDetail) {

                        finalizeSaveNote("Note Deleted.")

                        activity?.runOnUiThread {
                            requireActivity().onBackPressed()
                        }
                    }

                    true
                }

                R.id.saveNote -> {

                    saveTapped()

                    true
                }

                else -> false
            }
        }
    }

    private fun setupBottomNav() {
        // Bind Bottom nav bar options
        bottomNavBar = binding.noteDetailBottomNav
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
    }

    private fun saveTapped() {

        if (hasTextEditBeenEdited()) {
            val note = args.noteDetail

            note.content = binding.noteDetailContent.text.toString()

            note.title = binding.noteDetailTitle.text.toString()

            sharedNoteDetailViewModel.updateNote(
                note
            ) { finished ->

                if (finished) {
                    binding.noteDetailToolbarTitle.text = binding.noteDetailTitle.text.toString()
                    finalizeSaveNote("Note Updated")
                }
            }
        }
    }

    private fun finalizeSaveNote(message: String){
        activity?.runOnUiThread {

            hideKeyboard()

            showToast(message)
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

            startActivityForResult(photoIntent, IMAGE_CAPTURE_CODE)
        }
    }

    // Handle image pick result (Set image and get thumbnail)
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?) {

        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_PICK_CODE)) {

            data?.data?.let { uri ->

                sharedNoteDetailViewModel.currentPair = NotesImageUtils.preparePathForTempImage(uri, requireContext())

                val obj = DBNoteUIImage(
                    noteId = args.noteDetail.id,
                    saveButton = true,
                    cancelButton = true,
                )

                showFullScreenFragment(obj)
            }
        }

        if((resultCode == Activity.RESULT_OK) && (requestCode == IMAGE_CAPTURE_CODE)) {

            val photo = data?.extras?.get("data") as Bitmap
            photo.let {

                sharedNoteDetailViewModel.currentPair = Pair(it, null)

                val obj = DBNoteUIImage(
                    noteId = args.noteDetail.id,
                    saveButton = true,
                    cancelButton = true,
                )
                showFullScreenFragment(obj)
            }
        }
    }

    private fun showFullScreenFragment(imageObj: DBNoteUIImage) {

        val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToDBNoteImageFragment(imageObj)
        findNavController().navigate(action)
    }

    // Storage permission
    private fun askStoragePermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                DBPermissionConstants.ReadExternalStorage ) == PackageManager.PERMISSION_GRANTED){
            return true

        } else {
            val permissions = arrayOf(DBPermissionConstants.ReadExternalStorage)
            requestPermissions(permissions, DBPermissionConstants.readExternalStoragePermissionCode)
        }

        return false
    }

    // Camera permission
    private fun askCameraPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                DBPermissionConstants.CameraPermission) == PackageManager.PERMISSION_GRANTED) {
            return true

        } else {

            val permissions = arrayOf(DBPermissionConstants.CameraPermission)
            requestPermissions(
                permissions,
                DBPermissionConstants.cameraPermissionCode)
        }

        return false
    }

    private fun hasTextEditBeenEdited(): Boolean {
        return args.noteDetail.title == binding.noteDetailTitle.text.toString()
                || args.noteDetail.content == binding.noteDetailContent.text.toString()
    }

    override fun remove(obj: DBImage) {
        val newImageObj = DBNoteUIImage(
            id = obj.id,
            imagePath = obj.imgPath,
            deleteButton = true,
            doneButton = true,
        )

        showFullScreenFragment(newImageObj)
    }
}
