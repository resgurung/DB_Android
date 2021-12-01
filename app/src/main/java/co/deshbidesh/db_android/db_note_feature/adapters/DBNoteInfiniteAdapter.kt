package co.deshbidesh.db_android.db_note_feature.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import co.deshbidesh.db_android.databinding.LayoutInfiniteLoadingBinding
import co.deshbidesh.db_android.databinding.LayoutNoteListItemBinding
import co.deshbidesh.db_android.db_note_feature.infinite_list.ItemTrackListener
import co.deshbidesh.db_android.db_note_feature.infinite_list.LoadMore
import co.deshbidesh.db_android.db_note_feature.infinite_list.LoadMoreModel
import co.deshbidesh.db_android.db_note_feature.models.DBNoteInfiniteAdapterModel
import co.deshbidesh.db_android.db_note_feature.ui.DBInfiniteLoadingViewHolder
import co.deshbidesh.db_android.db_note_feature.ui.DBNoteItemViewHolder


class DBNoteInfiniteAdapter(var infiniteList: MutableList<DBNoteInfiniteAdapterModel>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {

        private const val VIEW_TYPE_DATA        = 0

        private const val VIEW_TYPE_PROGRESS    = 1
    }

    var loadMoreModel: LoadMoreModel? = null

    var loadMore: LoadMore? = null

    var itemTrackListener: ItemTrackListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            VIEW_TYPE_DATA -> {
                //inflates note row layout
                val binding = LayoutNoteListItemBinding.inflate(inflater, parent, false)
                DBNoteItemViewHolder(binding)
            }
            VIEW_TYPE_PROGRESS -> {
                //inflates progressbar layout
                val binding = LayoutInfiniteLoadingBinding.inflate(inflater, parent, false)
                DBInfiniteLoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Different View type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val row = infiniteList[position]
        when(row.type){
            VIEW_TYPE_DATA ->{
                if (holder is DBNoteItemViewHolder) {
                    row.note?.let {

                        holder.bind(it)

                        holder.itemView.setOnClickListener { _ ->

                            itemTrackListener?.noteItemClick(it, position)
                        }
                    }
                }
            }
            VIEW_TYPE_PROGRESS -> {
                if (holder is DBInfiniteLoadingViewHolder) {

                    holder.loadingItem.setOnClickListener {

                        loadMoreModel?.let {

                            loadMore?.loadMore(
                                infiniteList,
                                it
                            )
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = infiniteList.size

    override fun getItemViewType(position: Int): Int {
        return when(infiniteList[position].type)
        {
            1  -> VIEW_TYPE_PROGRESS
            else -> VIEW_TYPE_DATA
        }
    }

    fun setFiniteList(
        loadMoreModel: LoadMoreModel?,
        loadMore: LoadMore
    ){
        this.loadMoreModel = loadMoreModel
        this.loadMore = loadMore
    }

    //fun lastItemIsLoadMore(): Boolean = infiniteList.last().type == VIEW_TYPE_PROGRESS
}