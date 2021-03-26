package co.deshbidesh.db_android.db_note_feature.adapters

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.fragments.NoteDetailFragmentDirections

class DBNoteDetailImageRecyclerAdapter(private val iDeleteImage: InterfaceDeleteImage)
    : RecyclerView.Adapter<DBNoteDetailImageRecyclerAdapter.DBNoteDetailImageViewHolder>() {

    interface InterfaceDeleteImage{
        fun handleDeleteImage(imgPath: String)
    }

    private val interfaceDeleteImage: InterfaceDeleteImage = iDeleteImage

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

        holder.itemView.setOnLongClickListener {
            showDeleteDialog(it.context, imagePathList[position])
            true
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

    private fun showDeleteDialog(context: Context, imgPath: String){

        AlertDialog.Builder(context)
            .setMessage("Are you sure you want to delete the image? Click save to complete deletion")
            .setPositiveButton("Yes"){
                    _, _ -> interfaceDeleteImage.handleDeleteImage(imgPath)
            }

            .setNegativeButton("Cancel"){
                    dialog, _ ->
                dialog.dismiss()
            }.show()
    }


    class DBNoteDetailImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        private val imgView: ImageView = itemView.findViewById(R.id.note_add_img_view)

        fun bindData(imgPath: String){
            imgView.setImageURI(Uri.parse(imgPath))
        }
    }

}