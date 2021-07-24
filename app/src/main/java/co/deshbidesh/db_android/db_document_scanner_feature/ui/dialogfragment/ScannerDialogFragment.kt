package co.deshbidesh.db_android.db_document_scanner_feature.ui.dialogfragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBImageThresholdType
import co.deshbidesh.db_android.shared.utility.DBDocScanConstants
import co.deshbidesh.db_android.shared.utility.DBPreferenceHelper
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider



class ScannerDialogFragment : DialogFragment() {

    companion object {

        const val TAG = "ScannerDialogFragment"

        private const val KEY_TITLE = "KEY_TITLE"

        private const val THRESHOLD = "ImageThresholdSetting"

        @JvmStatic
        fun newInstance(param1: String) =
                ScannerDialogFragment().apply {
                    arguments = bundleOf(
                            KEY_TITLE to param1
                    )
                }
    }

    private var okButton: Button? = null

    private var radioGroup: RadioGroup? = null

    private var singleSlider: Slider? = null

    private var doubleRangeSlider: RangeSlider? = null

    private var blockSlider: Slider? = null

    private var blockMeanSlider: Slider? = null

    var resultListener: ScannerDialogFragmentListener? = null

    private val imageProcessingType: DBImageThresholdType? by lazy{
        DBImageThresholdType.ofRaw(
            DBPreferenceHelper.getStoredInt(
                DBDocScanConstants.DBIMAGE_PROCESSING_KEY,
                DBDocScanConstants.DBIMAGE_PROCESSING_DEFAULT_VALUE))
    }

    var binaryNum: Float = DBPreferenceHelper.getStoredFloat(DBDocScanConstants.BINARY_THRESHOLD_SETTING_KEY,
        DBDocScanConstants.BINARY_THRESHOLD_SETTING_VALUE)

    var otsuPair = Pair(DBPreferenceHelper.getStoredFloat(DBDocScanConstants.OTSU_MIN_KEY,
        DBDocScanConstants.OTSU_MIN_VALUE),
        DBPreferenceHelper.getStoredFloat(DBDocScanConstants.OTSU_MAX_KEY, DBDocScanConstants.OTSU_MAX_VALUE))

    var blockSize = DBPreferenceHelper.getStoredInt(DBDocScanConstants.ADAPTIVE_BLOCKSIZE_KEY,
        DBDocScanConstants.ADAPTIVE_BLOCKSIZE_DEFAULT_VALUE)

    var meanOffset: Float = DBPreferenceHelper.getStoredFloat(DBDocScanConstants.ADAPTIVE_OFFSET_FROM_MEAN_KEY,
        DBDocScanConstants.ADAPTIVE_OFFSET_FROM_MEAN_DEFAULT_VALUE)

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_scanner_dialog, container, false)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView(view)
        setupClickListener()
    }

    private fun setupView(view: View) {

        okButton = view.findViewById(R.id.okButton)

        radioGroup = view.findViewById(R.id.radioGroup)

        singleSlider = view.findViewById(R.id.singleSlider)

        doubleRangeSlider = view.findViewById(R.id.doubleRangeSlider)

        blockSlider = view.findViewById(R.id.blockSlider)

        blockMeanSlider = view.findViewById(R.id.blockMeanSlider)

        when(imageProcessingType) {

            DBImageThresholdType.BINARY -> {

                view.findViewById<RadioButton>(R.id.binary)?.isChecked = true

                setupBinary()
            }

            DBImageThresholdType.ADAPTIVE -> {

                view.findViewById<RadioButton>(R.id.adaptive)?.isChecked = true

                setupAdaptive()
            }

            DBImageThresholdType.OTSU -> {

                view.findViewById<RadioButton>(R.id.otsu)?.isChecked = true

                setupOtsu()
            }

        }

    }

    private fun setupClickListener() {

        okButton?.setOnClickListener {

            dismiss()
        }

        radioGroup?.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener() { _, optionId ->

            when(optionId) {
                // handle binary
                R.id.binary -> {

                    DBImageThresholdType.BINARY.toRaw()?.let {

                        DBPreferenceHelper.storeInt(DBDocScanConstants.DBIMAGE_PROCESSING_KEY, it)
                    }

                    setupBinary()

                }

                R.id.adaptive -> {

                    DBImageThresholdType.ADAPTIVE.toRaw()?.let {

                        DBPreferenceHelper.storeInt(DBDocScanConstants.DBIMAGE_PROCESSING_KEY, it)
                    }

                    setupAdaptive()
                }

                // handle otsu
                R.id.otsu -> {

                    DBImageThresholdType.OTSU.toRaw()?.let {

                        DBPreferenceHelper.storeInt(DBDocScanConstants.DBIMAGE_PROCESSING_KEY, it)
                    }

                    setupOtsu()
                }
            }
        })
    }

    fun setupBinary() {

        singleSlider?.visibility = View.VISIBLE

        singleSlider?.value = binaryNum

        doubleRangeSlider?.visibility = View.GONE

        blockSlider?.visibility = View.GONE

        blockMeanSlider?.visibility = View.GONE

        singleSliderChangeListener()
    }

    fun setupAdaptive() {

        singleSlider?.visibility = View.GONE

        doubleRangeSlider?.visibility = View.GONE

        blockSlider?.visibility = View.VISIBLE

        blockSlider?.value = blockSize.toFloat()

        blockMeanSlider?.visibility = View.VISIBLE

        blockMeanSlider?.value = meanOffset

        adaptiveSlidersChangeListener()
    }

    fun setupOtsu() {

        singleSlider?.visibility = View.GONE

        doubleRangeSlider?.visibility = View.VISIBLE

        doubleRangeSlider?.values = arrayListOf(otsuPair.first, otsuPair.second)

        blockSlider?.visibility = View.GONE

        blockMeanSlider?.visibility = View.GONE

        doubleSliderChangeListener()
    }

    private fun singleSliderChangeListener() {

        singleSlider?.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {

            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped

                if (slider.value != binaryNum) {

                    DBPreferenceHelper.storeFloat(DBDocScanConstants.BINARY_THRESHOLD_SETTING_KEY, slider.value)

                    binaryNum = slider.value

                    Log.d("onStopTrackingTouch", slider.value.toString())

                    resultListener?.update(DBImageThresholdType.BINARY)
                }

            }
        })
    }

    private fun doubleSliderChangeListener() {

        doubleRangeSlider?.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener{

            override fun onStartTrackingTouch(slider: RangeSlider) { }

            override fun onStopTrackingTouch(slider: RangeSlider) {

                doubleRangeSlider?.values?.let {

                    val min = it[0]

                    val max = it[1]

                    if (min != otsuPair.first || max != otsuPair.second) {

                        DBPreferenceHelper.storeFloat(DBDocScanConstants.OTSU_MIN_KEY, min)

                        DBPreferenceHelper.storeFloat(DBDocScanConstants.OTSU_MAX_KEY, max)

                        otsuPair = Pair(min, max)

                        Log.d("From", min.toString())

                        Log.d("T0", max.toString())

                        resultListener?.update(DBImageThresholdType.OTSU)
                    }
                }
            }
        })
    }

    private fun adaptiveSlidersChangeListener() {

        blockSlider?.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {

            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                // Responds to when slider's touch event is being stopped

                if (slider.value != binaryNum) {

                    var number = slider.value.toInt()

                    if (number % 2 == 0) {

                        // even number, prevent using even number
                        number += 1

                    }
                    // we only want to use odd number, more than zero and less than 81

                    if (number <= 0) {

                        number += 1
                    }

                    if (number >= 81) {

                        number = 81
                    }

                    DBPreferenceHelper.storeInt(DBDocScanConstants.ADAPTIVE_BLOCKSIZE_KEY, number)

                    blockSlider?.value = number.toFloat()

                    blockSize = number

                    Log.d("onStopTrackingTouch", slider.value.toString())

                    resultListener?.update(DBImageThresholdType.ADAPTIVE)
                }

            }
        })

        blockMeanSlider?.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {

            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                if (slider.value != binaryNum) {

                    DBPreferenceHelper.storeFloat(DBDocScanConstants.ADAPTIVE_OFFSET_FROM_MEAN_KEY, slider.value)

                    meanOffset = slider.value

                    Log.d("onStopTrackingTouch", slider.value.toString())

                    resultListener?.update(DBImageThresholdType.ADAPTIVE)
                }
            }

        })
    }


    interface ScannerDialogFragmentListener {

        fun update(type: DBImageThresholdType)
    }

}