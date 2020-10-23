package co.deshbidesh.db_android.OnboardingFeature.Screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import co.deshbidesh.db_android.R
import kotlinx.android.synthetic.main.fragment_fourth_screen.view.*


class FourthScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_fourth_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.onboarding_view_pager)

        view.fourth_scene_finish_button.setOnClickListener {

            findNavController().navigate(R.id.action_onboardingViewPager_to_homeFragment)

            onBoardingFinished()
        }

        return view
    }

    private fun onBoardingFinished() {

        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)

        val editor = sharedPref.edit()

        editor.putBoolean("Finished", true)

        editor.apply()
    }

}