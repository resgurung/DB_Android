package co.deshbidesh.db_android.db_note_feature.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.databinding.LayoutNoteListItemBinding
import co.deshbidesh.db_android.db_note_feature.fragments.NoteListFragmentDirections
import co.deshbidesh.db_android.db_note_feature.models.DBNote


class DBNoteListPagingDataAdapter(
): PagingDataAdapter<DBNote, DBNoteListPagingDataAdapter.NoteItemViewHolder>(
    DIFF_CALLBACK) {

    // Private inner note view holder class
    inner class NoteItemViewHolder(val binding: LayoutNoteListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(note: DBNote) {

            binding.noteListItemTitle.text = note.title

            binding.noteListItemDescription.text = note.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = LayoutNoteListItemBinding.inflate(inflater, parent, false)

        return NoteItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {

        when (holder) {

            is NoteItemViewHolder -> {

                getItem(position)?.let { note ->

                    holder.bind(note)

                    holder.itemView.setOnClickListener { view ->

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