package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Size
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbDocScanResultProcessBinding
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanSaveObject
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.PolygonView
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.SharedViewModel
import co.deshbidesh.db_android.shared.extensions.setBrightnessContrast
import co.deshbidesh.db_android.shared.extensions.setSaturation
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.util.*


class DBDocScanResultProcessFragment :
        Fragment(),
        PolygonView.PolygonViewMoveTracker,
        SeekBar.OnSeekBarChangeListener {

    enum class DisplayImage {
        ORIGINAL, GREY, BW
    }

    companion object {

        const val TAG = "ResultProcessFragment"
    }

    private var _binding: FragmentDbDocScanResultProcessBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var mainDisplayingImage: DisplayImage? = null
        set(value) {

            field = value

            when(value) {
                DisplayImage.ORIGINAL -> {

                    setUpOriginal()
                }
                DisplayImage.GREY -> {

                    setUpGrey()
                }
                DisplayImage.BW -> {

                    setUpBlackAndWhite()
                }
            }
        }

    private var bufferBitmap: Bitmap? = null
        set(value) {

            field = value

            setupUI()
        }

    private lateinit var polygonView: PolygonView

    private var squareCropViewShowing: Boolean = false

    // Seekbar tracker
    private var brightnessProgress = 0f

    private var contrastProgress = 1f

    private var saturationProgress = 1f

    private var blockValue = 11

    private var meanOffsetValue = 2.0

    private lateinit var lastRoute: SharedViewModel.Route

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbDocScanResultProcessBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        polygonView = view.findViewById(R.id.squarePolygonView)

        polygonView.hideSideImageView()

        polygonView.moveTracker = this

        binding.resultFragmentToolbar.setOnMenuItemClickListener { item ->

            when(item.itemId) {

                R.id.doc_scan_crop_menu -> {

                    showHideSquareCropView()

                    true
                }
                R.id.doc_scan_save_menu -> {

                    save()
                    true
                }
                else -> false
            }
        }

        binding.originalCardView.setOnClickListener {

            if (mainDisplayingImage != DisplayImage.ORIGINAL) {

                mainDisplayingImage = DisplayImage.ORIGINAL
            }

            showSetting(false)
        }

        binding.greyCardView.setOnClickListener {

            if (mainDisplayingImage != DisplayImage.GREY) {

                mainDisplayingImage = DisplayImage.GREY
            }

            showSetting(false)
        }

        binding.blackAndWhiteCardView.setOnClickListener {

            if (mainDisplayingImage != DisplayImage.BW) {

                mainDisplayingImage = DisplayImage.BW
            }

            showSetting(false)
        }

        binding.originalEditButton.setOnClickListener {

            buttonSetOnClickListener(it as Button)

        }

        binding.cropImageButton.setOnClickListener {

            getCroppedImage()
        }

        updateRoute()

        binding.greyEditButton.setOnClickListener {  } // Not used

        binding.blackAndWhiteEditButton.setOnClickListener { } // Not used

        setOnSeekBarChangeListener()

        showSetting(false)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun updateRoute() {

        lastRoute = sharedViewModel.getRoute()

        when(lastRoute) {

            SharedViewModel.Route.CAMERA_FRAGMENT -> {

                bufferBitmap = sharedViewModel.getFirstBitmap()?.copy(
                        sharedViewModel.getFirstBitmap()?.config,
                        true)
            }
            SharedViewModel.Route.INTERN_FRAGMENT -> {

                bufferBitmap = sharedViewModel.getSecondBitmap()?.copy(
                        sharedViewModel.getSecondBitmap()?.config,
                        true)

            } else -> false
        }

        mainDisplayingImage = DisplayImage.ORIGINAL

        sharedViewModel.setRoute(SharedViewModel.Route.RESULT_FRAGMENT)
    }

    private fun setOnSeekBarChangeListener() {

        binding.seekBarBrightness.setOnSeekBarChangeListener(this)

        binding.seekBarContrast.setOnSeekBarChangeListener(this)

        binding.seekBarSaturation.setOnSeekBarChangeListener(this)
    }

    private fun buttonSetOnClickListener(
            button: Button) {

        if (button.tag == 0) {

            button.tag = 1

            showSetting(true)

            button.text = resources.getText(R.string.closeButtonText)

        } else {

            button.tag = 0

            showSetting(false)

            button.text =  resources.getText(R.string.editButtonText)
        }
    }

    private fun currentImageViewsScreenBitmap(
            displayImage: DisplayImage
    ): Bitmap {

        return when(displayImage) {

            DisplayImage.ORIGINAL -> {

                (binding.originalImageView.drawable as BitmapDrawable).bitmap
            }
            DisplayImage.GREY -> {

                (binding.greyImageView.drawable as BitmapDrawable).bitmap
            }
            DisplayImage.BW -> {

                (binding.blackAndWhiteImageView.drawable as BitmapDrawable).bitmap
            }
        }
    }

    private fun setUpOriginal() {

        activity?.runOnUiThread {

            binding.originalTextView.setBackgroundColor(resources.getColor(R.color.blue, null))

            binding.greyTextView.setBackgroundColor(resources.getColor(R.color.greyish, null))

            binding.blackAndWhiteTextView.setBackgroundColor(resources.getColor(R.color.greyish, null))

            binding.originalEditButton.isVisible = true

            binding.originalEditButton.tag = 0

            binding.originalEditButton.text =  resources.getText(R.string.editButtonText)

            binding.greyEditButton.isVisible = false

            binding.blackAndWhiteEditButton.isVisible = false

            binding.originalSettingView.isVisible = true

            binding.scannedImage.setImageBitmap(currentImageViewsScreenBitmap(DisplayImage.ORIGINAL))
        }
    }

    private fun setUpGrey() {

        activity?.runOnUiThread {

            binding.originalTextView.setBackgroundColor(resources.getColor(R.color.greyish, null))

            binding.greyTextView.setBackgroundColor(resources.getColor(R.color.blue, null))

            binding.blackAndWhiteTextView.setBackgroundColor(resources.getColor(R.color.greyish, null))

            binding.originalEditButton.isVisible = false

            binding.greyEditButton.isVisible = false

            binding.blackAndWhiteEditButton.isVisible = false

            binding.scannedImage.setImageBitmap(currentImageViewsScreenBitmap(DisplayImage.GREY))
        }
    }

    private fun setUpBlackAndWhite() {

        activity?.runOnUiThread {

            binding.originalTextView.setBackgroundColor(resources.getColor(R.color.greyish, null))

            binding.greyTextView.setBackgroundColor(resources.getColor(R.color.greyish, null))

            binding.blackAndWhiteTextView.setBackgroundColor(resources.getColor(R.color.blue, null))

            binding.originalEditButton.isVisible = false

            binding.greyEditButton.isVisible = false

            binding.blackAndWhiteEditButton.isVisible = false

            binding.originalSettingView.isVisible = false

            binding.blackAndWhiteEditButton.tag = 0

            binding.blackAndWhiteEditButton.text =  resources.getText(R.string.editButtonText)

            binding.scannedImage.setImageBitmap(currentImageViewsScreenBitmap(DisplayImage.BW))
        }
    }

    private fun showSetting(
            setting: Boolean
    ) {

        binding.settingCardView.isVisible = setting
    }

    private fun setupUI() {

        bufferBitmap?.let {

            activity?.runOnUiThread {

                binding.originalImageView.setImageBitmap(it)

                updateGreyImageView()

                updateBWImageView()
            }
        }

        showSetting(false)
    }

    private fun updateGreyImageView() {

        val curr = currentImageViewsScreenBitmap(DisplayImage.ORIGINAL)

        sharedViewModel.convertRGBToGrey(curr) { grey ->

            updateGreyUI(grey)
        }
    }

    private fun updateBWImageView() {

        val curr = currentImageViewsScreenBitmap(DisplayImage.ORIGINAL)

        sharedViewModel.convertRGBToBW(
                curr,
                blockValue,
                meanOffsetValue) { bw ->

            updateBWUI(bw)
        }
    }

    private suspend fun updateGreyUI(
            bitmap: Bitmap?
    ) {

        withContext(Main) {

            binding.greyImageView.setImageBitmap(bitmap)
        }
    }

    private suspend fun updateBWUI(
            bitmap: Bitmap?
    ) {

        withContext(Main) {

            binding.blackAndWhiteImageView.setImageBitmap(bitmap)
        }
    }

    override fun onProgressChanged(
            seekBar: SeekBar?,
            progress: Int,
            fromUser: Boolean
    ) {

        when {
            seekBar!!.id == R.id.seekBar_brightness -> {

                brightnessProgress = progress.toFloat() - 255

            }
            seekBar.id == R.id.seekBar_contrast -> {

                contrastProgress = progress.toFloat() / 10f
            }
            seekBar.id == R.id.seekBar_saturation -> {

                saturationProgress = progress.toFloat() / 10
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) { }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

        when (seekBar!!.id) {
            R.id.seekBar_brightness -> {

                bufferBitmap?.apply {

                    binding.originalImageView.setImageBitmap(
                            setBrightnessContrast(
                                    brightnessProgress
                            )
                    )

                    binding.scannedImage.setImageBitmap(
                            currentImageViewsScreenBitmap(
                                    DisplayImage.ORIGINAL
                            )
                    )

                    updateGreyImageView()

                    updateBWImageView()
                }

            }
            R.id.seekBar_contrast -> {

                bufferBitmap?.apply {

                    binding.originalImageView.setImageBitmap(
                            setBrightnessContrast(
                                    0f,
                                    contrastProgress
                            )
                    )

                    binding.scannedImage.setImageBitmap(
                            currentImageViewsScreenBitmap(
                                    DisplayImage.ORIGINAL
                            )
                    )

                    updateGreyImageView()

                    updateBWImageView()
                }
            }
            R.id.seekBar_saturation -> {

                bufferBitmap?.apply {

                    binding.originalImageView.setImageBitmap(
                            setSaturation(
                                    saturationProgress
                            )
                    )
                    binding.scannedImage.setImageBitmap(
                            currentImageViewsScreenBitmap(
                                    DisplayImage.ORIGINAL
                            )
                    )

                    updateGreyImageView()

                    updateBWImageView()
                }

            }
        }
    }

    private fun getCroppedImage() {

        val points = polygonView.points

        sharedViewModel.cropImage(
                bufferBitmap,
                Size(
                        binding.scannedImage.width,
                        binding.scannedImage.height
                ),
                Pair(
                        PointF(points[0]!!.x, points[0]!!.y),
                        PointF(points[3]!!.x, points[3]!!.y)
                )
        ) {

            if (it != null) {

                showImageViewHolder()

                bufferBitmap = it

                mainDisplayingImage = DisplayImage.ORIGINAL
            }
            else {

                showImageViewHolder()
            }
        }
    }

    private fun showHideSquareCropView() {

        if (squareCropViewShowing) {

            polygonView.isVisible = false

            squareCropViewShowing = false

            binding.cropButtonViewHolder.isVisible = false

            binding.imageViewsViewHolder.isVisible = true

        } else {

            view?.post {

                binding.cropButtonViewHolder.isVisible = true

                binding.imageViewsViewHolder.isVisible = false

                squareCropViewShowing = true

                drawPolygon(
                        defaultOutlinePoints(currentScreenBitmap()),
                        binding.scanImageViewHolder
                )
            }

        }
    }

    private fun drawPolygon(
            points: Map<Int, PointF>,
            view: View) {

        polygonView.points =  points

        polygonView.visibility = View.VISIBLE

        val padding = resources.getDimension(R.dimen.scanPadding).toInt()

        val layoutParams = FrameLayout.LayoutParams(
                view.width - (2 * padding),
                view.height - (2 * padding)
        )

        layoutParams.gravity = Gravity.CENTER

        polygonView.layoutParams = layoutParams
    }

    private fun defaultOutlinePoints(
            tempBitmap: Bitmap
    ): Map<Int, PointF> {

        val outlinePoints: MutableMap<Int, PointF> = HashMap()

        outlinePoints[0] = PointF(
                0f,
                0f
        )

        outlinePoints[1] = PointF(
                binding.scannedImage.width.toFloat(),
                0f
        )

        outlinePoints[2] = PointF(
                0f,
                binding.scannedImage.height.toFloat()
        )

        outlinePoints[3] = PointF(
                binding.scannedImage.width.toFloat(),
                binding.scannedImage.height.toFloat())

        return outlinePoints
    }

    private fun currentScreenBitmap(): Bitmap = (binding.scannedImage.drawable as BitmapDrawable).bitmap

    private fun showImageViewHolder() {

        activity?.runOnUiThread {

            polygonView.isVisible = false

            squareCropViewShowing = false

            binding.cropButtonViewHolder.isVisible = false

            binding.imageViewsViewHolder.isVisible = true
        }
    }

    private fun save() {

        mainDisplayingImage?.let {

            val action = DBDocScanResultProcessFragmentDirections.actionDBDocScanResultProcessFragmentToDBDocScanSaveFragment(
                    DBDocScanSaveObject(
                        lastRoute,
                            currentImageViewsScreenBitmap(
                                    it
                            )
                    )
            )

            findNavController().navigate(action)
        }
    }

    override fun didMove() {

    }
}
