package co.deshbidesh.db_android.db_note_feature.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.databinding.LayoutNoteListItemBinding
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.db_note_feature.ui.DBNoteItemViewHolder

class DBNoteMonthAdapter(var noteList: MutableList<DBNote>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var itemClicked: DBNoteInterfaceItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutNoteListItemBinding.inflate(inflater, parent, false)

        return DBNoteItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val row = noteList[position]

        if (holder is DBNoteItemViewHolder) {

            holder.bind(row)

            holder.itemView.setOnClickListener { _ ->

                itemClicked?.itemClicked(row)
            }

        }
    }

    override fun getItemCount(): Int = noteList.size

    interface DBNoteInterfaceItemClick {

        fun itemClicked(item: DBNote)
    }
}