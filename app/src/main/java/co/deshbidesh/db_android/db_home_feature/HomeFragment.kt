package co.deshbidesh.db_android.db_home_feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.shared.DBAppBarConfiguration
import co.deshbidesh.db_android.shared.DBBaseFragment


class HomeFragment : DBBaseFragment() {

    private lateinit var toolBar: Toolbar

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar = view.findViewById(R.id.home_fragment_toolbar)

        navController = NavHostFragment.findNavController(this);

        NavigationUI.setupWithNavController(toolBar, navController, DBAppBarConfiguration.configuration())
    }

}