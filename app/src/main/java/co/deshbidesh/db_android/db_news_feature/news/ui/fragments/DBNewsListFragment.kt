package co.deshbidesh.db_android.db_news_feature.news.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbNewsListBinding

import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_network.domain.DBNewsRepository
import co.deshbidesh.db_android.db_news_feature.news.ui.adapter.DBNewsArticlePagingAdapter
import co.deshbidesh.db_android.db_news_feature.news.ui.adapter.DBNewsLoadStateAdapter
import co.deshbidesh.db_android.db_news_feature.news.viewmodel.DBNewsArticleViewModel
import co.deshbidesh.db_android.db_news_feature.news.viewmodel.DBNewsArticleViewModelFactory
import co.deshbidesh.db_android.shared.DBBaseFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class DBNewsListFragment : DBBaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    private var _binding: FragmentDbNewsListBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModelFactory: DBNewsArticleViewModelFactory

    private lateinit var viewModel: DBNewsArticleViewModel

    private lateinit var newsListPagingAdapter: DBNewsArticlePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbNewsListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newsListToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        newsListPagingAdapter = DBNewsArticlePagingAdapter()

        viewModelFactory = DBNewsArticleViewModelFactory(DBNewsRepository(DBDatabase.getDatabase(requireContext())))

        viewModel = ViewModelProvider(this, viewModelFactory).get(DBNewsArticleViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        binding.list.layoutManager = GridLayoutManager(context, 1)

        binding.list.addItemDecoration(decoration)

        binding.list.adapter = newsListPagingAdapter.withLoadStateFooter(
            footer = DBNewsLoadStateAdapter { newsListPagingAdapter.retry() }
        )

        newsListPagingAdapter.addLoadStateListener { loadState ->

            // show empty list
            val isListEmpty = loadState.refresh is LoadState.NotLoading && newsListPagingAdapter.itemCount == 0
            showEmptyList(isListEmpty)

            // Only show the list if refresh succeeds, either from the the local db or the remote.
            binding.list.isVisible =  loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
            // Show loading spinner during initial load or refresh.
            binding.progressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            // Show the retry state if initial load or refresh fails and there are no items.
            binding.retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error && newsListPagingAdapter.itemCount == 0
            // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error
            errorState?.let {
                Toast.makeText(
                    context,
                    "\uD83D\uDE28 Wooops ${it.error}",
                    Toast.LENGTH_LONG
                ).show()
            }

            subscribeUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }


    private fun subscribeUI() {

        lifecycleScope.launch {

            viewModel.getPagedList.distinctUntilChanged().collectLatest { it ->

                it.let {

                    newsListPagingAdapter.submitData(it)
                }
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            binding.emptyList.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        } else {
            binding.emptyList.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }

}