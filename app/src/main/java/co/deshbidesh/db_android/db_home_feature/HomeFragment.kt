package co.deshbidesh.db_android.db_home_feature

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentHomeBinding
import co.deshbidesh.db_android.main.DBDocScanActivity
import co.deshbidesh.db_android.shared.DBAppBarConfiguration
import co.deshbidesh.db_android.shared.DBBaseFragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel


class HomeFragment : DBBaseFragment() {

    private lateinit var toolBar: Toolbar

    private lateinit var navController: NavController

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    //private val imageList = ArrayList<SlideModel>() // Create image list

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolBar = view.findViewById(R.id.home_fragment_toolbar)

        binding.homeNewsButton.setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_DBNewsListFragment)
        }

        binding.homeNoteCard.setOnClickListener {

            findNavController().navigate(R.id.action_homeFragment_to_noteListFragment)
        }

        binding.homeDocScannerCard.setOnClickListener {

            activity?.let{

                val intent = Intent (it, DBDocScanActivity::class.java)

                it.startActivity(intent)

            }
        }

        navController = NavHostFragment.findNavController(this);

        NavigationUI.setupWithNavController(toolBar, navController, DBAppBarConfiguration.configuration())

        // imageList.add(SlideModel("String Url" or R.drawable)
        // imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title

//        imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years."))
//        imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."))
//        imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that."))
//
//        val imageSlider = view.findViewById<ImageSlider>(R.id.image_slider)
//        imageSlider.setImageList(imageList)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}
