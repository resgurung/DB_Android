package co.deshbidesh.db_android.db_onboarding_feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.deshbidesh.db_android.db_onboarding_feature.adapters.DBOnboardingViewPagerAdapter
import co.deshbidesh.db_android.db_onboarding_feature.screens.FirstScreen
import co.deshbidesh.db_android.db_onboarding_feature.screens.FourthScreen
import co.deshbidesh.db_android.db_onboarding_feature.screens.SecondScreen
import co.deshbidesh.db_android.db_onboarding_feature.screens.ThirdScreen
import co.deshbidesh.db_android.R
import kotlinx.android.synthetic.main.fragment_onboarding_view_pager.view.*


class OnboardingViewPager : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding_view_pager, container, false)

        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen(),
            FourthScreen()
        )

        val adapter = DBOnboardingViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        view.onboarding_view_pager.adapter = adapter

        return view
    }


}