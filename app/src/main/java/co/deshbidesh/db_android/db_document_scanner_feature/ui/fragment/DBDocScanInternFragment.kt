package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDBDocScanInternBinding
import co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils.DBImageUtils
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanImage
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanURI
import co.deshbidesh.db_android.db_document_scanner_feature.model.EdgePoint
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.PolygonView
import com.robin.cameraxtutorial.camerax.viewmodel.SharedViewModel
import java.util.ArrayList
import java.util.HashMap


class DBDocScanInternFragment : Fragment() {

    companion object {

        const val TAG = "DBDocScanInternFragment"
    }

    private var _binding: FragmentDBDocScanInternBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private var bufferBitmap: Bitmap? = null
        set(value){

            field = value

            //showImage()
        }

    private var buffer: DBDocScanImage? = null
        set(value) {

            field =  value

            showImage()
        }

    private lateinit var polygonView: PolygonView

    private val args by navArgs<DBDocScanInternFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDBDocScanInternBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        polygonView = view.findViewById(R.id.polygonView)

        binding.internFragmentToolbar.setOnMenuItemClickListener {

            when(it.itemId) {

                R.id.rotate0 -> {

                    rotate(0)
                    true
                }
                R.id.rotate90 -> {

                    rotate(90)
                    true
                }
                R.id.rotate180 -> {

                    rotate(180)
                    true
                }
                R.id.rotate270 -> {

                    rotate(270)
                    true
                }
                else -> false
            }
        }

        binding.internFragmentRotateCropButton.setOnClickListener {

        }

        binding.internFragmentRotateLeftButton.setOnClickListener {

            buffer?.let {

                buffer = DBImageUtils.rotateByOrientation(it.bitmap, ExifInterface.ORIENTATION_TRANSVERSE)
            }
        }

        binding.internFragmentRotateRightButton.setOnClickListener {

            // TODO background thread
            buffer?.let {

                buffer = DBImageUtils.rotateByOrientation(it.bitmap, ExifInterface.ORIENTATION_ROTATE_90)
            }
        }

        binding.internFragmentRotateNextButton.setOnClickListener {

            val image = getCroppedImage()

            image?.let {

                sharedViewModel.addData(it)

                findNavController().navigate(R.id.action_dbDocScanInternFragment_to_DBDocScanResultFragment)
            }
        }

        args.uriHolder?.let {

            val absolutePath = DBImageUtils.getAbsolutePathFor(context, it.uri)

            Log.d(TAG, "Image Path: $absolutePath")

            DBDocScanCameraFragment.Coroutines.uriToBitmap(it.uri, requireContext().contentResolver) { bitmap ->


                bitmap?.let { currBitmap ->

                    buffer = DBImageUtils.rotateBitmapWith(absolutePath, currBitmap)
                }
            }
        }

        binding.holderImageCrop.post {

            //showPolygon()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    private fun rotate(degree: Int) {

        bufferBitmap?.let {

            bufferBitmap = DBImageUtils.rotateBitmap(it, degree)

            binding.internFragmentImageView.setImageBitmap(bufferBitmap)

            showPolygon()
        }
    }


    private fun showImage() {

        buffer?.let {

            val scaledBitmap = scaledBitmap(
                it.bitmap,
                binding.holderImageCrop.width,
                binding.holderImageCrop.height
            )

            activity?.runOnUiThread {

                binding.internFragmentImageView.setImageBitmap(scaledBitmap)
            }
        }
    }

    private fun showPolygon() {

        bufferBitmap?.let {

            val scaledBitmap = scaledBitmap(
                it,
                binding.holderImageCrop.width,
                binding.holderImageCrop.height
            )
            binding.internFragmentImageView.setImageBitmap(scaledBitmap)

//            val tempBitmap = (binding.internFragmentImageView.drawable as BitmapDrawable).bitmap
//
//            val pointFs = getEdgePoints(scaledBitmap)
//
//            polygonView.points = pointFs
//            polygonView.visibility = View.VISIBLE
//
//            val padding = resources.getDimension(R.dimen.scanPadding).toInt()
//
//            val layoutParams = FrameLayout.LayoutParams(
//                tempBitmap.width + 2 * padding,
//                tempBitmap.height + 2 * padding
//            )
//
//            layoutParams.gravity = Gravity.CENTER
//
//            polygonView.layoutParams = layoutParams

        }
    }

    private fun getCroppedImage(): Bitmap? {
        val points = polygonView.points
        val width: Float? = (bufferBitmap?.width)?.toFloat()
        val height: Float? = (bufferBitmap?.height)?.toFloat()

        if (width != null && height != null) {

            val xRatio: Float = width / binding.internFragmentImageView.width
            val yRatio: Float = height / binding.internFragmentImageView.height

            val x1 = points[0]!!.x * xRatio
            val x2 = points[1]!!.x * xRatio
            val x3 = points[2]!!.x * xRatio
            val x4 = points[3]!!.x * xRatio
            val y1 = points[0]!!.y * yRatio
            val y2 = points[1]!!.y * yRatio
            val y3 = points[2]!!.y * yRatio
            val y4 = points[3]!!.y * yRatio

            val point1 = EdgePoint(x1.toDouble(), y1.toDouble())
            val point2 = EdgePoint(x2.toDouble(), y2.toDouble())
            val point3 = EdgePoint(x3.toDouble(), y3.toDouble())
            val point4 = EdgePoint(x4.toDouble(), y4.toDouble())

            val edgePoint = arrayListOf(point1, point2, point3, point4)

            return sharedViewModel.opencvHelper.getScannedBitmap(bufferBitmap,
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

    private fun scaledBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        Log.d(TAG, "scaledBitmap")
        Log.d(TAG, "$width $height")
        val m = Matrix()
        m.setRectToRect(RectF(0f,
            0f,
            bitmap.width.toFloat(),
            bitmap.height.toFloat()),
            RectF(0f,
                0f,
                width.toFloat(),
                height.toFloat()),
            Matrix.ScaleToFit.CENTER)
        return Bitmap.createBitmap(bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            m, true)
    }

    private fun getEdgePoints(tempBitmap: Bitmap): Map<Int, PointF>? {
        Log.d(TAG, "getEdgePoints")
        val pointFs = getContourEdgePoints(tempBitmap)
        return orderedValidEdgePoints(tempBitmap, pointFs)
    }

    private fun getContourEdgePoints(tempBitmap: Bitmap): List<PointF> {

        val result: MutableList<PointF> = ArrayList()
        val point2f =  sharedViewModel.opencvHelper.processImageForPoints(tempBitmap)
        Log.d(TAG,"Points: $point2f")
        if (point2f != null) {
            val points = listOf(*point2f.toArray())

            for (i in points.indices) {
                Log.d(TAG,"Point: $i")
                result.add(PointF(points[i].x.toFloat(), points[i].y.toFloat()))
            }
        } else {
            Log.d(TAG,"No Points")
        }

        return result
    }

    private fun getOutlinePoints(tempBitmap: Bitmap): Map<Int, PointF> {
        Log.d(TAG, "getOutlinePoints")
        val outlinePoints: MutableMap<Int, PointF> = HashMap()
        outlinePoints[0] = PointF(0f, 0f)
        outlinePoints[1] = PointF(tempBitmap.width.toFloat(), 0f)
        outlinePoints[2] = PointF(0f, tempBitmap.height.toFloat())
        outlinePoints[3] = PointF(tempBitmap.width.toFloat(), tempBitmap.height.toFloat())
        return outlinePoints
    }

    private fun orderedValidEdgePoints(tempBitmap: Bitmap, pointFs: List<PointF>): Map<Int, PointF> {
        Log.v(TAG, "orderedValidEdgePoints")
        var orderedPoints = polygonView.getOrderedPoints(pointFs)
        if (!polygonView.isValidShape(orderedPoints)) {
            orderedPoints = getOutlinePoints(tempBitmap)
        }
        return orderedPoints
    }

    private fun scaleBitmapXXX(bm: Bitmap): Bitmap? {

        val maxHeight = 2024
        val maxWidth = 2024
        var bm = bm
        var width = bm.width
        var height = bm.height
        Log.v("Pictures", "Width and height are $width--$height")
        if (width > height) {
            // landscape
            val ratio = width.toFloat() / maxWidth
            width = maxWidth
            height = (height / ratio).toInt()
        } else if (height > width) {
            // portrait
            val ratio = height.toFloat() / maxHeight
            height = maxHeight
            width = (width / ratio).toInt()
        } else {
            // square
            height = maxHeight
            width = maxWidth
        }
        Log.v("Pictures", "after scaling Width and height are $width--$height")
        bm = Bitmap.createScaledBitmap(bm, width, height, true)
        return bm
    }

}