package co.deshbidesh.db_android.db_settings_feature.ui

import android.content.res.AssetManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.databinding.FragmentPrivacyViewBinding
import co.deshbidesh.db_android.db_network.domain.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.factories.DBSettingsViewModelFactory
import co.deshbidesh.db_android.db_settings_feature.viewmodel.DBSettingsViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.utility.DBHTMLHelper

class PrivacyViewFragment : DBBaseFragment() {

    private var _binding: FragmentPrivacyViewBinding? = null

    private val binding get() = _binding!!

    private val args: PrivacyViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPrivacyViewBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.settingsPrivacyFragToolbar.setNavigationOnClickListener {

            requireActivity().onBackPressed()
        }

        val htmlContent = DBHTMLHelper.htmlHelper(args.privacy)

        binding.privacyView.setBackgroundColor(Color.TRANSPARENT)

        binding.privacyView.settings.javaScriptEnabled = true

        binding.privacyView.settings.javaScriptCanOpenWindowsAutomatically = true

        binding.privacyView.loadDataWithBaseURL(
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