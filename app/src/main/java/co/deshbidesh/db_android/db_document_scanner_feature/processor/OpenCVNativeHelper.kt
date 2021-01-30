package com.robin.cameraxtutorial.documentscanner

import android.graphics.Bitmap
import android.graphics.PointF
import android.media.Image
import android.util.Log
import com.robin.cameraxtutorial.helper.DBImageUtils
import com.robin.cameraxtutorial.helper.DBMathUtils
import com.robin.cameraxtutorial.helper.EdgePoint
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil

class OpenCVNativeHelper {

    companion object {

        const val TAG = "OpenCVNativeHelper"

        private const val AREA_LOWER_THRESHOLD = 0.20
        private const val AREA_UPPER_THRESHOLD = 0.99
        private const val DOWNSCALE_IMAGE_SIZE = 400f
        private const val CANNY_THRESHOLD_LOWERBOUND = 10.0
        private const val CANNY_THRESHOLD_UPPERBOUND = 300.0 // CANNY_THRESHOLD_LOWERBOUND * 3 (3 is kernal size)

        private val angleThreshold = 5
        private const val epsilon = 0.02//0.02

        // rect around the document (color: green)
        private val scalar = Scalar(0.0, 255.0, 0.0, 0.0)
        // thickness of the rect
        private const val thickness = 2
        // gaussian blur size
        private val gaussianBlurSize = Size(5.0, 5.0)
    }

    fun getScannedBitmap(image: Image, points: List<EdgePoint>): Bitmap? {

        val perspective = PerspectiveTransformation()

        val rectangle = MatOfPoint2f()

        rectangle.fromArray(points[0].toOpenCVPoint(),
                points[1].toOpenCVPoint(),
                points[2].toOpenCVPoint(),
                points[3].toOpenCVPoint())

        var dstMat = perspective.transform(DBImageUtils.jpegToMat(image), rectangle)

        val bitmap = DBImageUtils.matToBitmap(dstMat)

        dstMat.release()

        return bitmap
    }

    fun getScannedBitmap(rect: MatOfPoint2f, mat: Mat): Mat {

        val pt = PerspectiveTransformation()

        return pt.transform(mat, rect)
    }

    private val areaDescendingComparator: Comparator<MatOfPoint2f?> = Comparator<MatOfPoint2f?> { o1, o2 ->
        val area1 = Imgproc.contourArea(o1)

        val area2 = Imgproc.contourArea(o2)

        ceil(area2 - area1).toInt()
    }

    private fun isQuadrilateral(polygon: MatOfPoint2f, srcArea: Int): Boolean {

        if (polygon.rows() != 4) {

            return false
        }

        val area = abs(Imgproc.contourArea(polygon))

        Log.d(TAG, "Area: $area")
        Log.d(TAG, "Lower: ${srcArea * AREA_LOWER_THRESHOLD}")
        Log.d(TAG, "Upper: ${srcArea * AREA_UPPER_THRESHOLD}")

        if (area < srcArea * AREA_LOWER_THRESHOLD || area > srcArea * AREA_UPPER_THRESHOLD) {

            return false
        }

        return true
    }


    fun drawLinesOnMat(srcMat: Mat, points: MutableList<Point>): Mat {

        Imgproc.line(
                srcMat,
                points[0],
                points[1],
                scalar,
                thickness
        )
        Imgproc.line(
                srcMat,
                points[1],
                points[2],
                scalar,
                thickness
        )
        Imgproc.line(
                srcMat,
                points[2],
                points[3],
                scalar,
                thickness
        )
        Imgproc.line(
                srcMat,
                points[3],
                points[0],
                scalar,
                thickness
        )

        return srcMat

    }

    fun getPointsFromMat(srcMat: Mat): MatOfPoint2f? {

        Log.d(TAG, "Src Mat: ${srcMat.width()}x${srcMat.height()}")

        val ratio = DOWNSCALE_IMAGE_SIZE / srcMat.width().coerceAtLeast(srcMat.height())

        Log.d(TAG, "Ratio: $ratio")

        val downscaledSize = Size((srcMat.width() * ratio).toDouble(), (srcMat.height() * ratio).toDouble())

        var downscaled = Mat(downscaledSize, srcMat.type())

        Log.d(TAG, "DownscaledSize: ${downscaled.width()}x${downscaled.height()}")

        Imgproc.resize(srcMat, downscaled, downscaledSize)

        downscaled= runGaussianBlur(downscaled)

        downscaled = runCannyEdge(downscaled)

        val rectangles = runFindContours(downscaled)

        Log.d(TAG, "Rectangles: $rectangles")

        downscaled.release()

        if (rectangles.isNotEmpty()) {

            Collections.sort(rectangles, areaDescendingComparator)

            var rect = rectangles.first()

            rect = DBMathUtils.scaleRectangle(rect, (1f / ratio).toDouble())

            return rect

        }
        return null
    }

    /** Image Processing PipeLine */

    // Public

    fun runRotate(srcMat: Mat, rotation: Int): Mat {

        val destMat = Mat()

        Core.rotate(srcMat, destMat, rotation /*Core.ROTATE_90_CLOCKWISE*/)

        return destMat
    }

    fun getRectFrom(matOfPoint2f: MatOfPoint2f): Rect {

        return Imgproc.boundingRect(matOfPoint2f)
    }

    fun runGreyScale(srcMat: Mat): Mat{

        val destMat = Mat()

        Imgproc.cvtColor(srcMat, destMat, Imgproc.COLOR_RGB2GRAY)

        return destMat
    }

    fun runGreyScaleBitmap(bitmap: Bitmap): Bitmap {

        var tmpMat = DBImageUtils.bitmapToMat(bitmap)

        tmpMat = runGreyScale(tmpMat)

        val tmpBitmap = DBImageUtils.matToBitmap(tmpMat)

        tmpMat.release()

        return tmpBitmap
    }

    private fun runBinaryThreshold(srcMat: Mat, value: Double): Mat {

        var destMat = Mat()

        Imgproc.threshold(srcMat, destMat, value, 255.0, Imgproc.THRESH_BINARY)

        return destMat
    }

    fun runBinaryThresholdBitmap(bitmap: Bitmap, value: Float): Bitmap {

        var srcMat = DBImageUtils.bitmapToMat(bitmap)

        srcMat = runGreyScale(srcMat)

        srcMat = runBinaryThreshold(srcMat, value.toDouble())

        val tmpBitmap = DBImageUtils.matToBitmap(srcMat)

        srcMat.release()

        return tmpBitmap
    }

    private fun runAdaptiveThreshold(srcMat: Mat, blockSize: Int, meanoffset: Double): Mat {

        var destMat = Mat()
//        Imgproc.adaptiveThreshold(srcMat, destMat, 255.0, Imgproc.ADAPTIVE_THRESH_MEAN_C,
//                Imgproc.THRESH_BINARY, 15, 40.0)

        Imgproc.adaptiveThreshold(srcMat,
                destMat,
                255.0,
                Imgproc.ADAPTIVE_THRESH_MEAN_C,//Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY,
                blockSize, meanoffset)

        return destMat
    }

    fun runAdaptiveThresholdBitmap(bitmap: Bitmap, blockSize: Int, meanoffset: Float): Bitmap {

        var srcMat = DBImageUtils.bitmapToMat(bitmap)

        srcMat = runGreyScale(srcMat)

        srcMat = runAdaptiveThreshold(srcMat, blockSize, meanoffset.toDouble())//runBinaryThreshold(srcMat, max.toDouble())

        val tmpBitmap = DBImageUtils.matToBitmap(srcMat)

        srcMat.release()

        return tmpBitmap
    }

    private fun runOTSUsThreshold(srcMat: Mat, min: Double, max: Double): Mat {

        var dstMat = Mat()

        Imgproc.threshold(srcMat,
                dstMat,
                min, // threshold value, ignored when using cv2.THRESH_OTSU
                max, // maximum value assigned to pixel values exceeding the threshold
                Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU) // thresholding type

        return dstMat

    }

    fun runOTSUThresholdBitmap(bitmap: Bitmap, min: Float, max: Float): Bitmap {

        var srcMat = DBImageUtils.bitmapToMat(bitmap)

        srcMat = runGreyScale(srcMat)

        srcMat = runOTSUsThreshold(srcMat, min.toDouble(), max.toDouble())

        val tmpBitmap = DBImageUtils.matToBitmap(srcMat)

        srcMat.release()

        return tmpBitmap
    }

    // private
    private fun runGaussianBlur(srcMat: Mat): Mat {

        val blurredMat = Mat()

        Imgproc.GaussianBlur(srcMat, blurredMat, gaussianBlurSize, 2.0)

        return blurredMat
    }

    private fun runCannyEdge(srcMat: Mat): Mat {

        val cannyMat = Mat()

        Imgproc.Canny(srcMat, cannyMat, CANNY_THRESHOLD_LOWERBOUND, CANNY_THRESHOLD_UPPERBOUND)

        return cannyMat
    }

    private fun runFindContours(srcMat: Mat): List<MatOfPoint2f> {

        val srcArea = srcMat.rows() * srcMat.cols()

        var contours = mutableListOf<MatOfPoint>()

        var rectangles = mutableListOf<MatOfPoint2f>()

        val hierarchy = Mat()

        Imgproc.findContours(
                srcMat,
                contours,
                hierarchy,
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        )

        //sort contours by their size to find the biggest one, that's the bubble-sheet
        //val sortedContours = contours.sortedWith(compareBy { contourArea(it) }).asReversed()

        val approx = MatOfPoint2f()

        for (i in contours.indices) {
            val c = contours[i]
            val mop2f = MatOfPoint2f()
            c.convertTo(mop2f, CvType.CV_32F)
            val peri = Imgproc.arcLength(mop2f, true)

            Imgproc.approxPolyDP(mop2f, approx, epsilon * peri, true)

            Log.d(TAG, "Is Quad: ${isQuadrilateral(approx, srcArea)}")
            if (isQuadrilateral(approx, srcArea)) { // rectangle found

                rectangles.add(approx)

                break
            }

            mop2f.release()
        }

        hierarchy.release()

        return rectangles
    }
}
/*
Src Mat: 720x1280
D/OpenCVNativeHelper: Ratio: 0.3125
D/OpenCVNativeHelper: DownscaledSize: 225x400
D/OpenCVNativeHelper: Area: 18201.5
D/OpenCVNativeHelper: Lower: 18000.0
D/OpenCVNativeHelper: Upper: 89100.0
D/OpenCVNativeHelper: Is Quad: true
D/OpenCVNativeHelper: Area: 18201.5
D/OpenCVNativeHelper: Lower: 18000.0
 */