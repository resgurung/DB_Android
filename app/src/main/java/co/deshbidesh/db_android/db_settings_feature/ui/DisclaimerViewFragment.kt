package co.deshbidesh.db_android.db_settings_feature.ui

import android.content.res.AssetManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDisclaimerViewBinding
import co.deshbidesh.db_android.db_network.repository.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.factories.DBSettingsViewModelFactory
import co.deshbidesh.db_android.db_settings_feature.viewmodel.DBSettingsViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment

class DisclaimerViewFragment : DBBaseFragment() {

    var binding: FragmentDisclaimerViewBinding? = null

    private lateinit var dbSettingsViewModel: DBSettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDisclaimerViewBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context.let {

            binding?.settingsDisclaimerFragToolbar?.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        val dbSettingsViewModelFactory = DBSettingsViewModelFactory(DBSettingsRepository())
        dbSettingsViewModel = ViewModelProvider(this, dbSettingsViewModelFactory).get(DBSettingsViewModel::class.java)


        val manager: AssetManager = requireContext().assets

        dbSettingsViewModel.getSettingsDataTest(manager) {
            binding?.disclaimerView?.loadData(it.data[0].legalDisclaimer, "text/html", "utf-8")
        }
    }
}