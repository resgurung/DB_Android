package co.deshbidesh.db_android.db_note_feature.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.LayoutNoteListItemBinding
import co.deshbidesh.db_android.db_note_feature.fragments.NoteListFragmentDirections
import co.deshbidesh.db_android.db_note_feature.models.DBNote


class DBNoteRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // inner note view holder class
    inner class NoteViewHolder(val binding: LayoutNoteListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(note: DBNote) {

            binding.noteListItemTitle.text = note.title

            binding.noteListItemDescription.text = note.description
        }
    }

    private var noteItems: List<DBNote> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = LayoutNoteListItemBinding.inflate(inflater, parent, false)

        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {

            is NoteViewHolder -> {

                holder.bind(noteItems[position])

                holder.itemView.setOnClickListener { view ->

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