package co.deshbidesh.db_android.db_home_feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import co.deshbidesh.db_android.databinding.FragmentDbHomeInnerListBinding
import co.deshbidesh.db_android.db_database.database.DBDatabase
import co.deshbidesh.db_android.db_home_feature.adapter.DBHomeInnerListAdapter
import co.deshbidesh.db_android.db_network.domain.DBNewsRepository
import co.deshbidesh.db_android.db_news_feature.news.models.DBNewsUiModel
import co.deshbidesh.db_android.db_news_feature.news.viewmodel.DBNewsArticleViewModel
import kotlinx.coroutines.launch


@ExperimentalPagingApi
class DBHomeInnerListFragment : Fragment() {

    private var _binding: FragmentDbHomeInnerListBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: DBHomeInnerListAdapter

    //private val sharedNewsViewModel: DBNewsArticleViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbHomeInnerListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        if (sharedNewsViewModel.repository == null) {
//            sharedNewsViewModel.repository = DBNewsRepository(DBDatabase.getDatabase(requireContext()))
//        }

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        adapter = DBHomeInnerListAdapter()

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.homeInnerNewsList.layoutManager = layoutManager//GridLayoutManager(context, 1)

        binding.homeInnerNewsList.adapter = adapter

        binding.homeInnerNewsList.addItemDecoration(decoration)

        showProgressBar(true)

        //subscribeUI()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun showProgressBar(show: Boolean) {

        binding.progressBar.isVisible = show

        binding.homeInnerNewsList.isVisible = !show
    }

    private fun subscribeUI() {

//        lifecycleScope.launch {
//
//            val itemList:MutableList<DBNewsUiModel> = ArrayList()
//
//            sharedNewsViewModel.getRecentArticleWithCategory() { list ->
//
//                activity?.runOnUiThread {
//
//                    adapter.loadData(list)
//
//                    showProgressBar(false)
//                }
//
//            }
//
//        }
    }

}