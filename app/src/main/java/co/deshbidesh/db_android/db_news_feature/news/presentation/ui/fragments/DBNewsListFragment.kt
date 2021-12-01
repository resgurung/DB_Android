package co.deshbidesh.db_android.db_news_feature.news.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import co.deshbidesh.db_android.databinding.FragmentDbNewsListBinding
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.adapter.DBNewsArticlePagingAdapter
import co.deshbidesh.db_android.db_news_feature.news.presentation.ui.adapter.DBNewsLoadStateAdapter
import co.deshbidesh.db_android.db_news_feature.news.presentation.viewmodel.DBNewsArticleViewModel
import co.deshbidesh.db_android.db_news_feature.news.presentation.viewmodel.DBNewsArticleViewModelFactory
import co.deshbidesh.db_android.main.MainActivity
import co.deshbidesh.db_android.shared.DBBaseFragment
import com.google.android.gms.ads.AdRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.coroutines.CoroutineContext


@ExperimentalPagingApi
class DBNewsListFragment : DBBaseFragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main.immediate + SupervisorJob()

    override var bottomNavigationViewVisibility = View.GONE

    private var _binding: FragmentDbNewsListBinding? = null

    private val binding get() = _binding!!

    private lateinit var newsListPagingAdapter: DBNewsArticlePagingAdapter

    private var job: Job? = null

    private val sharedNewsViewModel: DBNewsArticleViewModel by activityViewModels { DBNewsArticleViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbNewsListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //job?.cancel()

        binding.newsListToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        // add dividers between RecyclerView's row items
        //val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        //binding.list.addItemDecoration(decoration)

        binding.list.layoutManager = GridLayoutManager(context, 1)

        binding.list.itemAnimator = null

        newsListPagingAdapter = DBNewsArticlePagingAdapter()

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
        }

//        val adMobRequest = AdRequest
//            .Builder()
//            .build()
//
//        binding.newsListBannerAdView.loadAd(adMobRequest)
    }

    override fun onStart() {
        super.onStart()

        job = viewLifecycleOwner.lifecycleScope.launchWhenStarted {

            sharedNewsViewModel.connect().distinctUntilChanged().collectLatest { pagingData ->

                newsListPagingAdapter.submitData(pagingData)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        job?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()

        job?.cancel()

        _binding = null
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