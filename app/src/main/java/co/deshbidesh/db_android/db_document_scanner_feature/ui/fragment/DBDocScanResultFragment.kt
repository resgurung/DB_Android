package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDBDocScanResultBinding
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBImageThresholdType
import co.deshbidesh.db_android.db_document_scanner_feature.ui.dialogfragment.ScannerDialogFragment
import co.deshbidesh.db_android.shared.utility.DBDocScanConstant
import co.deshbidesh.db_android.shared.utility.DBPreferenceHelper
import com.robin.cameraxtutorial.camerax.viewmodel.SharedViewModel


class DBDocScanResultFragment : Fragment(), ScannerDialogFragment.ScannerDialogFragmentListener {

    companion object {

        const val TAG = "DBDocScanResultFragment"

    }
    private var _binding: FragmentDBDocScanResultBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var greybitmap: Bitmap? = null

    private var originalBitmap: Bitmap? = null

    private var thresholdBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDBDocScanResultBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scannedImage.setBackgroundColor(Color.rgb(100, 100, 50))

        sharedViewModel.bitmapdata.observe(viewLifecycleOwner) {

            setupOriginalImage(it)
        }

        binding.resultFragmentToolbar.setOnMenuItemClickListener {

            when(it.itemId) {

                R.id.blackAndWhiteSetting -> {

                    openThresholdSetting()

                    true }
                R.id.cropImage -> {

                    true
                }
                else -> false
            }
        }

        binding.doneButton.setOnClickListener {

            // save to notes here
            //it.findNavController().navigate(R.id.action_BFragment_to_AFragment)
        }

        binding.blackAndWhite.setOnClickListener {

            update(DBImageThresholdType.ADAPTIVE)
        }

        binding.original.setOnClickListener {

            binding.scannedImage.setImageBitmap(originalBitmap)
        }

        binding.grayMode.setOnClickListener {

            // turn the original into grey here
            originalBitmap?.let { it1 ->

                if (greybitmap == null) {

                    greybitmap = sharedViewModel.opencvHelper.runGreyScaleBitmap(it1)
                }

                binding.scannedImage.setImageBitmap(greybitmap)

            } ?: run {

                Log.e(TAG, "Image not found, maybe image not set." )
            }
        }
    }

    private fun setupOriginalImage(bitmap: Bitmap) {

        originalBitmap = bitmap

        greybitmap = null

        binding.scannedImage.setImageBitmap(bitmap)
    }

    override fun update(type: DBImageThresholdType) {

        originalBitmap?.let {

            when(type) {

                DBImageThresholdType.BINARY -> {

                    val binaryMax = DBPreferenceHelper.getStoredFloat(
                        DBDocScanConstant.BINARY_THRESHOLD_SETTING_KEY,
                        DBDocScanConstant.BINARY_THRESHOLD_SETTING_VALUE)

                    thresholdBitmap = sharedViewModel.opencvHelper.runBinaryThresholdBitmap(it, binaryMax)
                }

                DBImageThresholdType.ADAPTIVE -> {

                    val blockSize = DBPreferenceHelper.getStoredInt(
                        DBDocScanConstant.ADAPTIVE_BLOCKSIZE_KEY,
                        DBDocScanConstant.ADAPTIVE_BLOCKSIZE_DEFAULT_VALUE)

                    val offset = DBPreferenceHelper.getStoredFloat(
                        DBDocScanConstant.ADAPTIVE_OFFSET_FROM_MEAN_KEY,
                        DBDocScanConstant.ADAPTIVE_OFFSET_FROM_MEAN_DEFAULT_VALUE)

                    Log.d(TAG, "Block Size: $blockSize")
                    thresholdBitmap = sharedViewModel.opencvHelper.runAdaptiveThresholdBitmap(it, blockSize, offset)
                }

                DBImageThresholdType.OTSU -> {

                    val min = DBPreferenceHelper.getStoredFloat(
                        DBDocScanConstant.OTSU_MIN_KEY,
                        DBDocScanConstant.OTSU_MIN_VALUE)

                    val max = DBPreferenceHelper.getStoredFloat(
                        DBDocScanConstant.OTSU_MAX_KEY,
                        DBDocScanConstant.OTSU_MAX_VALUE)

                    thresholdBitmap = sharedViewModel.opencvHelper.runOTSUThresholdBitmap(it, min, max)
                }
            }

            activity?.runOnUiThread {

                binding.scannedImage.setImageBitmap(thresholdBitmap)
            }
        }

    }


    private fun openThresholdSetting(){

        val dialogFragment = ScannerDialogFragment.newInstance("Title")

        dialogFragment.resultListener = this

        dialogFragment.show(requireActivity().supportFragmentManager,"")
    }

}