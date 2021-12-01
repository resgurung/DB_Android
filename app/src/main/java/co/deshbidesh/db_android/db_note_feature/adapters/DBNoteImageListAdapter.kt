package co.deshbidesh.db_android.db_note_feature.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.databinding.LayoutNoteImgRowItemBinding
import co.deshbidesh.db_android.db_note_feature.models.DBImage


class DBNoteImageListAdapter(
    noteImageInterface: DBNoteImageInterface
): ListAdapter<DBImage, RecyclerView.ViewHolder>(DiffCallback()) {

    class DBNoteImageViewHolder(
        val binding: LayoutNoteImgRowItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(obj: DBImage) {

            binding.noteImageView.setImageURI(Uri.parse(obj.imgPath))
        }
    }

    interface DBNoteImageInterface {
        fun remove(obj: DBImage)
    }

    private val interfaceNoteImage: DBNoteImageInterface = noteImageInterface

    private class DiffCallback : DiffUtil.ItemCallback<DBImage>() {

        override fun areItemsTheSame(oldItem: DBImage, newItem: DBImage) :Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DBImage, newItem: DBImage) =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = LayoutNoteImgRowItemBinding.inflate(inflater, parent, false)

        return DBNoteImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder) {

            is DBNoteImageViewHolder -> {

                val item = getItem(position)

                holder.bind(item)

                holder.itemView.setOnClickListener { view ->

                    interfaceNoteImage.remove(item)
                }
            }
        }
    }
}