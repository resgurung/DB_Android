package co.deshbidesh.db_android.db_settings_feature.ui

import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.databinding.FragmentPrivacyViewBinding
import co.deshbidesh.db_android.db_network.repository.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.factories.DBSettingsViewModelFactory
import co.deshbidesh.db_android.db_settings_feature.viewmodel.DBSettingsViewModel
import co.deshbidesh.db_android.shared.DBBaseFragment

class PrivacyViewFragment : DBBaseFragment() {

    var binding: FragmentPrivacyViewBinding? = null

    private lateinit var dbSettingsViewModel: DBSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPrivacyViewBinding.inflate(inflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context.let {
            binding?.settingsPrivacyFragToolbar?.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        val dbSettingsViewModelFactory = DBSettingsViewModelFactory(DBSettingsRepository())
        dbSettingsViewModel = ViewModelProvider(this, dbSettingsViewModelFactory).get(DBSettingsViewModel::class.java)


        val manager: AssetManager = requireContext().assets

        dbSettingsViewModel.getSettingsDataTest(manager) {
            binding?.privacyView?.loadData(it.data[0].privacy, "text/html", "utf-8")
        }
    }
}