package co.deshbidesh.db_android.db_note_feature.ui

import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.databinding.LayoutInfiniteLoadingBinding

class DBInfiniteLoadingViewHolder(
    val binding: LayoutInfiniteLoadingBinding
    ): RecyclerView.ViewHolder(binding.root) {
        val loadingItem = binding.infiniteProgressBarItem
        val container = binding.loadingContainer
}