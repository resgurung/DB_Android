package co.deshbidesh.db_android.db_onboarding_feature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import co.deshbidesh.db_android.databinding.FragmentOnboardingViewPagerBinding
import co.deshbidesh.db_android.db_onboarding_feature.adapters.DBOnboardingViewPagerAdapter
import co.deshbidesh.db_android.db_onboarding_feature.screens.FirstScreen
import co.deshbidesh.db_android.db_onboarding_feature.screens.SecondScreen


class OnboardingViewPager : Fragment() {

    private var binding: FragmentOnboardingViewPagerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOnboardingViewPagerBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen()
            //ThirdScreen(),
            //FourthScreen()
        )

        val adapter = DBOnboardingViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding?.onboardingViewPager?.adapter = adapter

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}