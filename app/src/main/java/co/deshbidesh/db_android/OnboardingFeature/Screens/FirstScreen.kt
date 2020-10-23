package co.deshbidesh.db_android.OnboardingFeature.Screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import co.deshbidesh.db_android.R
import kotlinx.android.synthetic.main.fragment_first_screen.view.*


class FirstScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_first_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.onboarding_view_pager)

        view.first_scene_next_button.setOnClickListener {

            viewPager?.currentItem = 1
        }

        return view
    }
}