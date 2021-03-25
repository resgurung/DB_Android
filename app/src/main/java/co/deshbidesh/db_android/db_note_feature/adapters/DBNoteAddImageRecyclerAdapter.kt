package co.deshbidesh.db_android.db_note_feature.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.fragments.NoteAddFragmentDirections
import co.deshbidesh.db_android.db_note_feature.fragments.NoteDetailFragmentDirections
import kotlinx.android.synthetic.main.note_add_img_row_item.view.*

class DBNoteAddImageRecyclerAdapter: RecyclerView.Adapter<DBNoteAddImageRecyclerAdapter.DBNoteAddImageViewHolder>() {

    private var imagePathList =  ArrayList<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DBNoteAddImageViewHolder {
        return DBNoteAddImageViewHolder(
            LayoutInflater.from(parent.context)
            .inflate(R.layout.note_add_img_row_item, parent, false))
    }

    override fun onBindViewHolder(holder: DBNoteAddImageViewHolder, position: Int) {
        holder.itemView.note_add_img_view.setImageURI(Uri.parse(imagePathList[position]))

        holder.itemView.setOnClickListener {
            val action = NoteAddFragmentDirections.actionNoteAddFragmentToFullscreenImageFragment(imagePathList[position])
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return imagePathList.size
    }

    fun setData(uriStringList: ArrayList<String>) {
        this.imagePathList = uriStringList
        notifyDataSetChanged()
    }

    class DBNoteAddImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){}
}