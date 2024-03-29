package co.deshbidesh.db_android.db_home_feature

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentHomeBinding
import co.deshbidesh.db_android.main.DBDocScanActivity
import co.deshbidesh.db_android.shared.DBAppBarConfiguration
import co.deshbidesh.db_android.shared.DBBaseFragment


class HomeFragment : DBBaseFragment() {

    private lateinit var toolBar: Toolbar

    private lateinit var navController: NavController

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar = view.findViewById(R.id.home_fragment_toolbar)

        binding.homeNoteCard.setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_noteListFragment)
        }

        binding.homeDocScannerCard.setOnClickListener {

            activity?.let{

                val intent = Intent (it, DBDocScanActivity::class.java)

                it.startActivity(intent)

            }
        }

        navController = NavHostFragment.findNavController(this);

        NavigationUI.setupWithNavController(toolBar, navController, DBAppBarConfiguration.configuration())
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}
/*
    A  -> A-> B -> C
 */