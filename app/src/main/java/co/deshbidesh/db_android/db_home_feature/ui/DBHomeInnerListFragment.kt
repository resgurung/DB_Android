package co.deshbidesh.db_android.db_home_feature.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbHomeInnerListBinding
import co.deshbidesh.db_android.db_home_feature.viewmodels.HomeViewModel
import co.deshbidesh.db_android.db_home_feature.viewmodels.HomeViewModelFactory
import co.deshbidesh.db_android.db_imageslider.constants.ScaleTypes
import co.deshbidesh.db_android.db_imageslider.interfaces.ItemClickListener
import co.deshbidesh.db_android.db_imageslider.models.SlideModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import co.deshbidesh.db_android.db_imageslider.interfaces.ItemChangeListener





@ExperimentalPagingApi
class DBHomeInnerListFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main.immediate + SupervisorJob()

    private var _binding: FragmentDbHomeInnerListBinding? = null

    private val binding get() = _binding!!

    private var imageList =  ArrayList<SlideModel>()

    private val homeViewModel: HomeViewModel by viewModels { HomeViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbHomeInnerListBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.allFeaturedArticles.observe(viewLifecycleOwner, { articleWithCategory ->

            val list = articleWithCategory.map {

                SlideModel(it.article.featured_image.large, it.article.title)
            }

            imageList = ArrayList(list)

            showSlider()
        })

//        binding.imageSlider.setOnClickListener {
//            findNavController().navigate(R.id.action_homeFragment_to_DBNewsListFragment)
//        }

        binding.imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {

                activity?.runOnUiThread {

                    findNavController().navigate(R.id.action_homeFragment_to_DBNewsListFragment)
                }
            }
        })

        /// item changed
//        binding.imageSlider.setItemChangeListener(object : ItemChangeListener {
//            override fun onItemChanged(position: Int) {
//                Log.e("myTag", "slide--> $position")
//            }
//        })

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun showSlider() {

        activity?.runOnUiThread {

            if (imageList.isEmpty()) {

                imageList.add(SlideModel(R.drawable.logo_landscape_500x244, "DeshBidesh App", ScaleTypes.FIT))

            } else {

                binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
            }

            binding.progressBar.isVisible = imageList.isEmpty()
        }
    }
}