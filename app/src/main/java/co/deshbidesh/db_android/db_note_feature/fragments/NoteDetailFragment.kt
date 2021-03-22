package co.deshbidesh.db_android.db_note_feature.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


class NoteDetailFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private val args by navArgs<NoteDetailFragmentArgs>()

    private lateinit var noteDetailViewModel: DBNoteDetailViewModel

    private lateinit var noteDetailViewModelFactory: DBNoteDetailViewModelFactory

    private lateinit var noteDetailImageAdapter: DBNoteDetailImageRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentNoteDetailBinding.inflate(inflater, container, false)

        context ?: return binding.root

        // Set adapter
        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        noteDetailImageAdapter = DBNoteDetailImageRecyclerAdapter()
        binding.detailNoteRecyclerView.layoutManager = layoutManager
        binding.detailNoteRecyclerView.adapter = noteDetailImageAdapter

        noteDetailViewModelFactory = DBNoteDetailViewModelFactory(
            DBNoteRepository(DBDatabase.getDatabase(requireContext()).noteDAO()),
            DBImageRepository(DBDatabase.getDatabase(requireContext()).imageDAO()),
            args.noteDetail)

        noteDetailViewModel = ViewModelProvider(this, noteDetailViewModelFactory).get(DBNoteDetailViewModel::class.java)



        binding.noteDetailToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        binding.noteDetailEditButton.setOnClickListener {

            val action = NoteDetailFragmentDirections.actionNoteDetailFragmentToNoteEditFragment(noteDetailViewModel.getNote())

            findNavController().navigate(action)
        }

        binding.noteDetailDeleteButton.setOnClickListener {

            // delete here
            noteDetailViewModel.deleteNote()

            showToast("Note deleted")

            findNavController().navigate(R.id.action_noteDetailFragment_to_noteListFragment)
        }

        binding.noteDetailTitle.text = noteDetailViewModel.getNote().title

        binding.noteDetailContent.text = noteDetailViewModel.getNote().content

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getImageList()
    }

    private fun getImageList(){

        val imagePathList: ArrayList<String> = arrayListOf()

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
}
