package co.deshbidesh.db_android.db_note_feature.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.fragments.NoteListFragmentDirections
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import kotlinx.android.synthetic.main.layout_note_list_item.view.*

class DBNoteListPagingDataAdapter(
): PagingDataAdapter<DBNote, DBNoteListPagingDataAdapter.NoteItemViewHolder>(
    DIFF_CALLBACK) {

    // Private inner note view holder class
    class NoteItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val titleTV: TextView = view.findViewById(R.id.note_list_item_title)

        private val descTV: TextView = view.findViewById(R.id.note_list_item_description)

        fun bind(note: DBNote) {

            titleTV.text = note.title

            descTV.text = note.description

            //Log.d("Note ->", "${note.id }, ${note.title}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {

        return NoteItemViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.layout_note_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {

        when (holder) {

            is NoteItemViewHolder -> {

                getItem(position)?.let { note ->

                    holder.bind(note)

                    holder.itemView.note_list_row_layout.setOnClickListener { view ->

                        val action =
                            NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(
                                note
                            )

                        view.findNavController().navigate(action)
                    }
                }
            }
        }
    }

    companion object {

        private  val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DBNote>() {

            override fun areItemsTheSame(oldItem: DBNote, newItem: DBNote): Boolean {

                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DBNote, newItem: DBNote): Boolean {

                return oldItem == newItem
            }
        }
    }
}