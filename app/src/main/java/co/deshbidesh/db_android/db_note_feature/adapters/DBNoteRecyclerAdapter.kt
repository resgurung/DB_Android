package co.deshbidesh.db_android.db_note_feature.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.fragments.NoteListFragmentDirections
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import kotlinx.android.synthetic.main.layout_note_list_item.view.*

class DBNoteRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // inner note view holder class
    class NoteViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val titleTV: TextView = view.findViewById(R.id.note_list_item_title)

        private val descTV: TextView = view.findViewById(R.id.note_list_item_description)

        fun bind(note: DBNote) {

            titleTV.text = note.title

            descTV.text = note.description
        }
    }

    private var noteItems: List<DBNote> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return NoteViewHolder (

            LayoutInflater.from(parent.context).inflate(R.layout.layout_note_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {

            is NoteViewHolder -> {

                holder.bind(noteItems[position])

                holder.itemView.note_list_row_layout.setOnClickListener { view ->

                    val action = NoteListFragmentDirections.actionNoteListFragmentToNoteDetailFragment(noteItems[position])

                    view.findNavController().navigate(action)
                }
            }
        }
    }

    override fun getItemCount(): Int {

        return noteItems.size
    }

    fun loadData(noteItems: List<DBNote>) {

        this.noteItems = noteItems

        notifyDataSetChanged()
    }
}