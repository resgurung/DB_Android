package co.deshbidesh.db_android.db_onboarding_feature.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentFourthScreenBinding


class FourthScreen : Fragment() {

    private var binding: FragmentFourthScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFourthScreenBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_fourth_screen, container, false)

        //val viewPager = activity?.findViewById<ViewPager2>(R.id.onboarding_view_pager)

        binding?.fourthSceneFinishButton?.setOnClickListener {}

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

}