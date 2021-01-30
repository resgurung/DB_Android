package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDBDocScanInternBinding

class DBDocScanInternFragment : Fragment() {

    private var _binding: FragmentDBDocScanInternBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDBDocScanInternBinding.inflate(layoutInflater)

        return binding.root
    }

}