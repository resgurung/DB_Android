package co.deshbidesh.db_android.db_news_feature.news.presentation.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import co.deshbidesh.db_android.databinding.FragmentDbNewsDetailBinding
import co.deshbidesh.db_android.shared.DBHelper
import co.deshbidesh.db_android.shared.extensions.isDarkThemeOn
import co.deshbidesh.db_android.shared.extensions.loadImage
import co.deshbidesh.db_android.shared.utility.DBHTMLHelper
import com.google.android.gms.ads.AdRequest


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

        binding.postImage.loadImage(args.news.featured_image.large)

        binding.postTitle.text = args.news.title

        binding.newsDetailToolbarTitle.text = args.news.title

        binding.postCategory.text = args.news.categories.first().name

        binding.postAuthor.text = "Author: ${args.news.author}"

        binding.postPublishedDate.text = "Published: ${DBHelper.formatDateForNews(args.news.published_at)}"

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

        DBHelper.webSettingForDarkMode(requireContext(), binding.postWebView)

//        DBHelper.requestAdmobBanner(binding.newsDetailAdView)

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}