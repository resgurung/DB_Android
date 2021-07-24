package co.deshbidesh.db_android.db_settings_feature.ui

import android.content.res.AssetManager
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

class DisclaimerViewFragment : DBBaseFragment() {

    private var _binding: FragmentDisclaimerViewBinding? = null

    private val binding get() = _binding!!

    private lateinit var dbSettingsViewModel: DBSettingsViewModel

    private val args: DisclaimerViewFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentDisclaimerViewBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        context.let {

            binding.settingsDisclaimerFragToolbar.setNavigationOnClickListener {
                requireActivity().onBackPressed()
            }
        }

        val dbSettingsViewModelFactory = DBSettingsViewModelFactory(DBSettingsRepository())
        dbSettingsViewModel = ViewModelProvider(this, dbSettingsViewModelFactory).get(DBSettingsViewModel::class.java)

        binding.disclaimerView.loadData(
            args.disclaimer,
            "text/html",
            "utf-8"
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}