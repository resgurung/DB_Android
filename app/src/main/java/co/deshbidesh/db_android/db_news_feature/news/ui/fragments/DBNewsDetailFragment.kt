package co.deshbidesh.db_android.db_news_feature.news.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbNewsDetailBinding
import co.deshbidesh.db_android.shared.utility.DBHTMLHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class DBNewsDetailFragment : Fragment() {

    private var _binding: FragmentDbNewsDetailBinding? = null

    private val binding get() = _binding!!

    private val args: DBNewsDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbNewsDetailBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.appBarLayout.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        println("-------------------------------------------------------")
        println("$args.news")

        Glide.with(requireContext())
            .load(args.news.featured_image.medium)
            .placeholder(R.drawable.ic_loading_image_placeholder)
            .error(R.drawable.ic_loading_broken_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.postImage)

        binding.postTitle.text = args.news.title

        binding.postCategory.text = args.news.categories[0].name

        val htmlContent = DBHTMLHelper.htmlHelper(args.news.content)

        binding.postWebView.setBackgroundColor(Color.TRANSPARENT)

        binding.postWebView.settings.javaScriptEnabled = true

        binding.postWebView.settings.javaScriptCanOpenWindowsAutomatically = true

        binding.postWebView.loadDataWithBaseURL(
            null,
            htmlContent,
            "text/html; charset=utf-8",
            "UTF-8",
            null
        )

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

}