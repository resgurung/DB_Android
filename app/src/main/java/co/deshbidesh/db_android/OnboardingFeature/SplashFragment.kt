package co.deshbidesh.db_android.OnboardingFeature

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.Shared.DBBaseFragment


class SplashFragment : DBBaseFragment() {

    // set the visibility of setting the bottomNavigationView.
    override var bottomNavigationViewVisibility = View.GONE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Handler(Looper.getMainLooper()).postDelayed({
            run {

                if (onBoardingFinished()) {

                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)

                } else {

                    findNavController().navigate(R.id.action_splashFragment_to_onboardingViewPager)
                }

            }
        }, 1500)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    private fun onBoardingFinished(): Boolean {

        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)

        return sharedPref.getBoolean("Finished", false)
    }

}