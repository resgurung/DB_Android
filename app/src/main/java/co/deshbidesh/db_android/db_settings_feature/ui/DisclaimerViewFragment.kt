package co.deshbidesh.db_android.db_settings_feature.ui

import android.content.res.AssetManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.databinding.FragmentDisclaimerViewBinding
import co.deshbidesh.db_android.db_network.domain.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.factories.DBSettingsViewModelFactory
import co.deshbidesh.db_android.db_settings_feature.viewmodel.DBSettingsViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment
import co.deshbidesh.db_android.shared.utility.DBHTMLHelper

class DisclaimerViewFragment : DBBaseFragment() {

    private var _binding: FragmentDisclaimerViewBinding? = null

    private val binding get() = _binding!!

    private val args: DisclaimerViewFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentDisclaimerViewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.settingsDisclaimerFragToolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val htmlContent = DBHTMLHelper.htmlHelper(args.disclaimer)

        binding.disclaimerView.setBackgroundColor(Color.TRANSPARENT)

        binding.disclaimerView.settings.javaScriptEnabled = true

        binding.disclaimerView.settings.javaScriptCanOpenWindowsAutomatically = true

        binding.disclaimerView.loadDataWithBaseURL(
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