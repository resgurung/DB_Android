package co.deshbidesh.db_android.db_note_feature.fragments

import android.app.AlertDialog
import android.content.Context
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
import java.io.File


class NoteDetailFragment : DBBaseFragment() {

    private var binding: FragmentNoteDetailBinding? = null

    private lateinit var toolbar: Toolbar

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteDetailFragmentArgs>()

    private var imagePathList: ArrayList<String> = arrayListOf()

    private lateinit var noteDetailViewModel: DBNoteDetailViewModel

    private lateinit var noteDetailViewModelFactory: DBNoteDetailViewModelFactory

    private lateinit var noteDetailImageAdapter: DBNoteDetailImageRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)

        //context ?: return binding!!.root

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        noteDetailImageAdapter = DBNoteDetailImageRecyclerAdapter()
        binding?.detailNoteRecyclerView?.layoutManager = layoutManager
        binding?.detailNoteRecyclerView?.adapter = noteDetailImageAdapter

        noteDetailViewModelFactory = DBNoteDetailViewModelFactory(
            DBNoteRepository(DBDatabase.getDatabase(requireContext()).noteDAO()),
            DBImageRepository(DBDatabase.getDatabase(requireContext()).imageDAO()),
            args.noteDetail)

        noteDetailViewModel = ViewModelProvider(this, noteDetailViewModelFactory).get(DBNoteDetailViewModel::class.java)


        binding?.noteDetailToolbar?.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }


        binding?.noteDetailTitle?.text = noteDetailViewModel.getNote().title

        binding?.noteDetailContent?.text = noteDetailViewModel.getNote().content


        toolbar = binding!!.noteDetailToolbar
        toolbar.inflateMenu(R.menu.db_note_detail_toolbar_menu)
        toolbar.setOnMenuItemClickListener { item ->

            when(item.itemId){
                R.id.deleteNote -> {

                    showDeleteDialog(requireContext())

                    true
                }

                R.id.editNote ->{

                    val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToNoteEditFragment(noteDetailViewModel.getNote())
                    findNavController().navigate(action)
                    true
                }

                else -> false
            }
        }

        getImageList()
    }


    private fun deleteImagesFromStorage() {

        for(path in imagePathList ) {
            val file = File(path)
            file.delete();
        }
    }

    private fun getImageList(){

        noteDetailViewModel.getImageListByNoteId {

            // Other thread
            if(it.isNotEmpty()){
                for(img in it){
                    val path = img.imgPath
                    imagePathList.add(path)
                }

                // UI Thread
                activity?.runOnUiThread {
                    noteDetailImageAdapter.setData(imagePathList)
                }
            }
        }
    }

    private fun showDeleteDialog(context: Context){

        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete the note?")
            .setPositiveButton("Yes"){
                    _, _ ->
                // delete here
                noteDetailViewModel.deleteNote { note ->   // delete note from database

                    if(note.imageIds != null) {

                        noteDetailViewModel.deleteImages(){     // delete images data from database if any

                            deleteImagesFromStorage()    // finally delete image from storage

                            showToast("Note deleted")

                            findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
                        }
                    } else {

                        showToast("Note deleted")

                        findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
                    }
                }
            }

            .setNegativeButton("Cancel"){
                    dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}
