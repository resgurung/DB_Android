package co.deshbidesh.db_android.db_settings_feature

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_network.network_util.NetworkConfiguration
import co.deshbidesh.db_android.db_network.repository.DBSettingsRepository
import co.deshbidesh.db_android.db_settings_feature.factories.DBSettingsViewModelFactory
import co.deshbidesh.db_android.db_settings_feature.viewmodel.DBSettingsViewModel
import co.deshbidesh.db_android.shared.DBAppBarConfiguration
import co.deshbidesh.db_android.shared.DBBaseFragment


class SettingsFragment : DBBaseFragment() {

    private lateinit var toolBar: Toolbar

    private lateinit var dbSettingsViewModel: DBSettingsViewModel

    var button: Button? = null

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar = view.findViewById(R.id.setting_fragment_toolbar)

        button = view.findViewById(R.id.button_test)

        navController = NavHostFragment.findNavController(this)

        NavigationUI.setupWithNavController(toolBar, navController, DBAppBarConfiguration.configuration())

        val dbSettingsViewModelFactory = DBSettingsViewModelFactory(DBSettingsRepository())
        dbSettingsViewModel = ViewModelProvider(this, dbSettingsViewModelFactory).get(DBSettingsViewModel::class.java)


        button?.setOnClickListener {

            if(NetworkConfiguration.isNetworkAvailable(requireContext())){
                dbSettingsViewModel.getSettingsData()
                dbSettingsViewModel.settingsData.observe(viewLifecycleOwner, Observer { response ->

                    if(response.isSuccessful){
                        Log.d("Response", response.body()?.data?.get(0).toString())
                        Log.d("Response", response.body()?.type.toString())
                    }else{
                        Log.d("Response", response.errorBody().toString())
                    }
                })
            }else{

                Toast.makeText(requireContext(),
                        "No internet connection.",
                        Toast.LENGTH_LONG)
                        .show()
            }
        }
    }
}