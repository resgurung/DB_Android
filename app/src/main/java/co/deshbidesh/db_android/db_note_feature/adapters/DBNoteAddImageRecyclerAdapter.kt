package co.deshbidesh.db_android.db_note_feature.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.databinding.NoteAddImgRowItemBinding
import co.deshbidesh.db_android.db_note_feature.fragments.NoteAddFragmentDirections


class DBNoteAddImageRecyclerAdapter: RecyclerView.Adapter<DBNoteAddImageRecyclerAdapter.DBNoteAddImageViewHolder>() {

    private var imagePathList =  ArrayList<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DBNoteAddImageViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = NoteAddImgRowItemBinding.inflate(inflater, parent, false)

        return DBNoteAddImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DBNoteAddImageViewHolder, position: Int) {


        holder.bind(Uri.parse(imagePathList[position]))

        holder.itemView.setOnClickListener {
//            val action = NoteAddFragmentDirections.actionNoteAddFragmentToFullscreenImageFragment(imagePathList[position])
//            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return imagePathList.size
    }

    fun setData(uriStringList: ArrayList<String>) {

        this.imagePathList = uriStringList

        notifyDataSetChanged()
    }

    inner class DBNoteAddImageViewHolder(val binding: NoteAddImgRowItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri) {

            binding.noteAddImgView.setImageURI(uri)
        }
    }
}