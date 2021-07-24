package co.deshbidesh.db_android.db_home_feature

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
import androidx.paging.map
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbHomeInnerListBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_home_feature.adapter.DBHomeInnerListAdapter
import co.deshbidesh.db_android.db_network.domain.DBNewsRepository
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsUiModel
import co.deshbidesh.db_android.db_news_feature.news.ui.adapter.DBNewsArticlePagingAdapter
import co.deshbidesh.db_android.db_news_feature.news.viewmodel.DBNewsArticleViewModel
import co.deshbidesh.db_android.db_news_feature.news.viewmodel.DBNewsArticleViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@ExperimentalPagingApi
class DBHomeInnerListFragment : Fragment() {

    private lateinit var viewModelFactory: DBNewsArticleViewModelFactory

    private lateinit var viewModel: DBNewsArticleViewModel

    private var _binding: FragmentDbHomeInnerListBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: DBHomeInnerListAdapter

    private lateinit var newsListPagingAdapter: DBNewsArticlePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbHomeInnerListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelFactory = DBNewsArticleViewModelFactory(DBNewsRepository(DBDatabase.getDatabase(requireContext())))

        viewModel = ViewModelProvider(this, viewModelFactory).get(DBNewsArticleViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        adapter = DBHomeInnerListAdapter()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.homeInnerNewsList.layoutManager = layoutManager//GridLayoutManager(context, 1)



        binding.homeInnerNewsList.adapter = adapter

        binding.homeInnerNewsList.addItemDecoration(decoration)

        subscribeUI()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun subscribeUI() {

        lifecycleScope.launch {

            val itemList:MutableList<DBNewsUiModel> = ArrayList()

            viewModel.getRecentArticleWithCategory() { ll ->
                println("--------- ll ----------")
                println(ll)
                ll.map {

                    println("------------it -----------")
                    println(it)
                    itemList.add(
                        DBNewsUiModel(
                            it.article.n_id,
                            it.article.post_id,
                            it.article.title,
                            it.article.description,
                            it.article.published_at,
                            it.article.featured_image.thumbnail
                        )
                    )
                }
                println("--------- itemList ----------")
                println(itemList)
                adapter.loadData(itemList)
            }

        }
    }

}