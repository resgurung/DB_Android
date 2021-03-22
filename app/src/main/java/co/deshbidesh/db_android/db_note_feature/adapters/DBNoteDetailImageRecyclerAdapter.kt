package co.deshbidesh.db_android.db_note_feature.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.fragments.NoteDetailFragmentDirections

class DBNoteDetailImageRecyclerAdapter: RecyclerView.Adapter<DBNoteDetailImageRecyclerAdapter
.DBNoteDetailImageViewHolder>() {

    private var imagePathList =  ArrayList<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DBNoteDetailImageViewHolder {
        return DBNoteDetailImageViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.note_add_img_row_item, parent, false))
    }

    override fun onBindViewHolder(holder: DBNoteDetailImageViewHolder, position: Int) {

        holder.bindData(imagePathList[position])

        holder.itemView.setOnClickListener {
            val action = NoteDetailFragmentDirections
                .actionNoteDetailFragmentToFullscreenImageFragment(imagePathList[position])
            it.findNavController().navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return imagePathList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    fun setData(uriStringList: ArrayList<String>?) {
        imagePathList = uriStringList!!
        notifyDataSetChanged()
    }

    class DBNoteDetailImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val imgView: ImageView = itemView.findViewById(R.id.note_add_img_view)

        fun bindData(imgPath: String){
            imgView.setImageURI(Uri.parse(imgPath))
        }
    }
}