package co.deshbidesh.db_android.db_note_feature.ui

import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.databinding.LayoutNoteListItemBinding
import co.deshbidesh.db_android.db_note_feature.models.DBNote
import co.deshbidesh.db_android.shared.DBHelper
import java.text.SimpleDateFormat
import java.util.*

class DBNoteItemViewHolder(val binding: LayoutNoteListItemBinding): RecyclerView.ViewHolder(binding.root)  {

    fun bind(note: DBNote) {

        binding.noteListItemTitle.text = note.title

        binding.noteListItemCreatedDate.text = "Created: ${DBHelper.formatDateForNote(note.createdDate)}"

        binding.noteListItemUpdatedDate.text = "Updated: ${DBHelper.formatDateForNote(note.updatedDate)}"
    }

}