package co.deshbidesh.db_android.db_onboarding_feature.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import co.deshbidesh.db_android.R
import kotlinx.android.synthetic.main.fragment_third_screen.view.*


class ThirdScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_third_screen, container, false)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.onboarding_view_pager)

        view.third_scene_next_button.setOnClickListener {

            viewPager?.currentItem = 3
        }
        return view
    }

}