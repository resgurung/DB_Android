package co.deshbidesh.db_android.db_news_feature.news.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.R

class DBNewsLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<DBNewsLoadStateAdapter.DBNewsLoadStateViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState) =
        DBNewsLoadStateViewHolder(parent, retry)

    override fun onBindViewHolder(holder: DBNewsLoadStateViewHolder, loadState: LoadState) =
        holder.bind(loadState)

    class DBNewsLoadStateViewHolder (
        parent: ViewGroup,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_news_load_state_view, parent, false)
    ) {
        private val progressBar: ProgressBar = itemView.findViewById(R.id.load_state_progress)
        private val errorMsg: TextView = itemView.findViewById(R.id.load_state_errorMessage)
        private val retry: Button = itemView.findViewById<Button>(R.id.load_state_retry)
            .also { it.setOnClickListener { retry.invoke() } }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                errorMsg.text = loadState.error.localizedMessage
            }
            progressBar.visibility = toVisibility(loadState is LoadState.Loading)
            retry.visibility = toVisibility(loadState !is LoadState.Loading)
            errorMsg.visibility = toVisibility(loadState !is LoadState.Loading)
        }

        private fun toVisibility(constraint: Boolean): Int = if (constraint) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}