package co.deshbidesh.db_android.db_onboarding_feature.screens

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

        binding?.secondSceneNextButton?.setOnClickListener {

            activity?.let{

                val intent = Intent (it, MainActivity::class.java)

                it.startActivity(intent)
            }
        }

        return binding?.root
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}