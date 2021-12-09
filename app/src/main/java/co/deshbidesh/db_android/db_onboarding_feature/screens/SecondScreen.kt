package co.deshbidesh.db_android.db_onboarding_feature.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentSecondScreenBinding
import co.deshbidesh.db_android.main.MainActivity


class SecondScreen : Fragment() {

    private var binding: FragmentSecondScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSecondScreenBinding.inflate(inflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.onboarding_view_pager)

        binding?.secondSceneNextButton?.setOnClickListener {

            viewPager?.currentItem = 2
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}