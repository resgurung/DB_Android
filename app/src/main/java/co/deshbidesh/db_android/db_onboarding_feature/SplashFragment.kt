package co.deshbidesh.db_android.db_onboarding_feature


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.main.MainActivity
import co.deshbidesh.db_android.shared.utility.DBPreferenceHelper


class SplashFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.getMainLooper()).postDelayed({
            run {

                if (DBPreferenceHelper.firstRun) {

                    DBPreferenceHelper.firstRun = false

                    navigateTo(R.id.action_DBSplashFragment_to_OnboardingViewPager)

                } else {

                    activity?.let{
                        val intent = Intent (it, MainActivity::class.java)
                        it.startActivity(intent)
                    }
                }
            }
        }, 2000)
    }

    private fun navigateTo(id: Int) {

        activity?.runOnUiThread {

            findNavController().navigate(id)
        }
    }
}