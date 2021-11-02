package co.deshbidesh.db_android.db_onboarding_feature.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentThirdScreenBinding

class ThirdScreen : Fragment() {

    private var binding: FragmentThirdScreenBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentThirdScreenBinding.inflate(inflater, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.onboarding_view_pager)

        binding?.thirdSceneNextButton?.setOnClickListener {

            viewPager?.currentItem = 3
        }
        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

}