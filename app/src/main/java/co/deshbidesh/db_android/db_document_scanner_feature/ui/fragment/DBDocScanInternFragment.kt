package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbDocScanInternBinding
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanImage
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.PolygonView
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.SharedViewModel
import co.deshbidesh.db_android.shared.extensions.showAlert
import java.util.*

/*
    *inward crop button
    *outward crop button

    ------------------------------------------------------------
    - fragment loads
    - buffer gets a image and orientation
    - image is shown in imageView
    - calculates points for overlay(polygon view)
    - if points is not null and has points then show outward free button
    - if no points or null points then show default polygon of the sie of imageView holder

    ------------------------------------------------------------
    - when polygon is moved

    - if free outward is shown do nothing
    - if inward crop button is showing then change to outward free button

    -------------------------------------------------------------

    - when free outward is shown
    - this means polygon has either moved or openCV has detected a square and square is showing on top of imageView

    - when free outward button is pressed
    - default polygon showing on screen and button changed to inward button showing

    ---------------------------------------------------------------

    - when inward crop is shown
    - this means no points is detected and waiting for user to move the polygon according to their use

    - user move the polygon
    - show outward crop button

    - user pressed inward crop button
    - if points is not empty then show polygon in respect to points, button = outward crop button
    - if points is empty then button button stays inward crop
 */
class DBDocScanInternFragment :
        Fragment(),
        PolygonView.PolygonViewMoveTracker {

    companion object {

        const val TAG = "DBDocScanInternFragment"
    }

    private var _binding: FragmentDbDocScanInternBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var buffer: DBDocScanImage? = null
        set(value) {

            field =  value

            showImage()
        }

    private var edgePoints: Map<Int, PointF>? = null

    private lateinit var polygonView: PolygonView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbDocScanInternBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        polygonView = view.findViewById(R.id.polygonView)

        polygonView.moveTracker = this

        binding.internFragmentRotateCropInwardButton.setOnClickListener {

            edgePoints?.let {

                Log.d(TAG, "inside -> $it")
                drawPolygon(it)

                showFreeCropButton()
            }
        }

        binding.internFragmentRotateCropOutwardButton.setOnClickListener {

            drawPolygon(defaultOutlinePoints(currentScreenBitmap()))

            showCroppedButton()
        }

        binding.internFragmentRotateLeftButton.setOnClickListener {

            buffer?.let {

                sharedViewModel.rotateByOrientation(it.bitmap, ExifInterface.ORIENTATION_ROTATE_270) { docScanImage ->

                    buffer = docScanImage
                }
            }
        }

        binding.internFragmentRotateRightButton.setOnClickListener {

            buffer?.let {

                sharedViewModel.rotateByOrientation(it.bitmap, ExifInterface.ORIENTATION_ROTATE_90) { docScanImage ->

                    buffer = docScanImage
                }
            }
        }

        binding.internFragmentNextButton.setOnClickListener {

            val image = getCroppedImage()

            if (image != null) {

                sharedViewModel.insertInSecond(image)

            } else {

                buffer?.let {

                    sharedViewModel.insertInSecond(it.bitmap)

                } ?: kotlin.run {

                    showAlert("Could not process the image")

                    requireActivity().onBackPressed()
                }
            }

            navigateTo()
        }

        Log.d(TAG, "uri xx pxy onViewCreated")
        updateRoute(view)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun updateRoute(view: View) {

        when(sharedViewModel.getRoute()) {

            SharedViewModel.Route.CAMERA_FRAGMENT -> {

                setupFromFirstBitmap(view)
            }
            SharedViewModel.Route.SELECTION_FRAGMENT -> {

                bitmapFromUri(view)

            }
            SharedViewModel.Route.RESULT_FRAGMENT -> {

                sharedViewModel.clearSecond()

                if(sharedViewModel.uri == null) {

                    setupFromFirstBitmap(view)

                } else {

                    bitmapFromUri(view)
                }
            }
            else -> {}
        }

        sharedViewModel.setRoute(SharedViewModel.Route.INTERN_FRAGMENT)
    }

    private fun setupFromFirstBitmap(view: View) {
        sharedViewModel.getFirstBitmap()?.let { bitmap ->

            view.post {

                val scaledBitmap = sharedViewModel.scaledBitmap(
                        bitmap,
                        binding.holderImageCrop.width,
                        binding.holderImageCrop.height
                )

                buffer = DBDocScanImage(scaledBitmap, 1)
            }


        } ?: run {

            // no image so go back
            requireActivity().onBackPressed()
        }
    }

    private fun bitmapFromUri(view: View){

        context?.let { thisContext ->

            sharedViewModel.uriToBitmap(thisContext) { bitmap, absPath ->

                if (bitmap != null) {

                    view.post {

                        val scaledBitmap = sharedViewModel.scaledBitmap(
                                bitmap,
                                binding.holderImageCrop.width,
                                binding.holderImageCrop.height
                        )

                        buffer = sharedViewModel.rotateImageIfRequired(absPath, scaledBitmap)
                    }

                } else {

                    Log.e(TAG, " error decoding image")
                }
            }
        }
    }

    private fun navigateTo() {

        findNavController().navigate(
                R.id.action_DBDocScanInternFragment_to_DBDocScanResultProcessFragment
        )
    }

    private fun showImage() {

        buffer?.let {

            activity?.runOnUiThread {

                binding.internFragmentImageView.setImageBitmap(it.bitmap)

                calculatePoints(currentScreenBitmap())
            }
        }
    }

    private fun drawPolygon(points: Map<Int, PointF>) {

        view?.post {

            polygonView.points =  points

            polygonView.visibility = View.VISIBLE

            val padding = resources.getDimension(R.dimen.scanPadding).toInt()

            val layoutParams = FrameLayout.LayoutParams(
                    currentScreenBitmap().width + 2 * padding,
                    currentScreenBitmap().height + 2 * padding
            )

            layoutParams.gravity = Gravity.CENTER

            polygonView.layoutParams = layoutParams
        }
    }

    private fun calculatePoints(bitmap: Bitmap) {

        getContourEdgePoints(bitmap) {

            if (it.isNotEmpty()) {

                edgePoints = it

                drawPolygon(it)

                showFreeCropButton()

            } else {

                drawPolygon(defaultOutlinePoints(currentScreenBitmap()))

                showCroppedButton()
            }

        }
    }

//    private fun allButtonsStatus(enabled: Boolean) {
//        binding.internFragmentRotateNextButton.isEnabled = enabled
//        binding.internFragmentRotateRightButton.isEnabled = enabled
//        binding.internFragmentRotateLeftButton.isEnabled = enabled
//    }

    private fun showCroppedButton() {

        activity?.runOnUiThread {

            binding.freeCardView.isVisible = false

            binding.cropCardView.isVisible = true
        }
    }

    private fun showFreeCropButton() {

        activity?.runOnUiThread {

            binding.freeCardView.isVisible = true

            binding.cropCardView.isVisible = false
        }

    }

    private fun getCroppedImage(): Bitmap? {

        val points = polygonView.points

        buffer?.let {

            val w = it.bitmap.width.toFloat()

            val h = it.bitmap.height.toFloat()

            val xRatio: Float = w / binding.internFragmentImageView.width
            val yRatio: Float = h / binding.internFragmentImageView.height

            val x1 = points[0]!!.x * xRatio
            val x2 = points[1]!!.x * xRatio
            val x3 = points[2]!!.x * xRatio
            val x4 = points[3]!!.x * xRatio
            val y1 = points[0]!!.y * yRatio
            val y2 = points[1]!!.y * yRatio
            val y3 = points[2]!!.y * yRatio
            val y4 = points[3]!!.y * yRatio

            return sharedViewModel.opencvHelper.getScannedBitmap(it.bitmap,
                    x1,
                    y1,
                    x2,
                    y2,
                    x3,
                    y3,
                    x4,
                    y4)
        }
        return null
    }

    private fun getContourEdgePoints(
            tempBitmap: Bitmap,
            listener: (Map<Int, PointF>) -> Unit
    ){

        sharedViewModel.processImageForPoints(tempBitmap) {

            val orderedPoints = polygonView.getOrderedPoints(it)

            if (!polygonView.isValidShape(orderedPoints)) {

                listener(mapOf())
            }

            listener(orderedPoints)
        }
    }

    private fun defaultOutlinePoints(
            tempBitmap: Bitmap
    ): Map<Int, PointF> {

        val outlinePoints: MutableMap<Int, PointF> = HashMap()

        outlinePoints[0] = PointF(0f, 0f)

        outlinePoints[1] = PointF(tempBitmap.width.toFloat(), 0f)

        outlinePoints[2] = PointF(0f, tempBitmap.height.toFloat())

        outlinePoints[3] = PointF(tempBitmap.width.toFloat(), tempBitmap.height.toFloat())

        return outlinePoints
    }

    private fun currentScreenBitmap(): Bitmap = (binding.internFragmentImageView.drawable as BitmapDrawable).bitmap

    override fun didMove() {

        if (!binding.freeCardView.isVisible) {

            binding.freeCardView.isVisible = true

            binding.cropCardView.isVisible = false
        }
    }
}