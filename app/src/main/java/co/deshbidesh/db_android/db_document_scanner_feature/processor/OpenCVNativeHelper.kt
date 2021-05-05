package co.deshbidesh.db_android.db_document_scanner_feature.processor

import android.graphics.Bitmap
import android.graphics.PointF
import android.media.Image
import android.util.Log
import co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils.DBImageUtils
import co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils.DBMathUtils
import co.deshbidesh.db_android.db_document_scanner_feature.model.EdgePoint
import co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment.DBDocScanInternFragment
import co.deshbidesh.db_android.shared.extensions.mapOrientation
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil


class OpenCVNativeHelper {

    companion object {

        const val TAG = "OpenCVNativeHelper"

        private const val DILATE_ERODE_SIZE = 3

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

        rectangle.fromArray(
            points[0].toOpenCVPoint(),
            points[1].toOpenCVPoint(),
            points[2].toOpenCVPoint(),
            points[3].toOpenCVPoint()
        )

        var dstMat = perspective.transform(DBImageUtils.jpegToMat(image), rectangle)

        val bitmap = DBImageUtils.matToBitmap(dstMat)

        dstMat.release()

        return bitmap
    }

    fun getScannedBitmapWith(srcMat: Mat, points: List<EdgePoint>): Bitmap? {

        val perspective = PerspectiveTransformation()

        val rectangle = MatOfPoint2f()

        rectangle.fromArray(
                points[0].toOpenCVPoint(),
                points[1].toOpenCVPoint(),
                points[2].toOpenCVPoint(),
                points[3].toOpenCVPoint()
        )

        var dstMat = perspective.transform(srcMat, rectangle)

        val bitmap = DBImageUtils.matToBitmap(dstMat)

        dstMat.release()

        return bitmap
    }

    fun getScannedBitmap(rect: MatOfPoint2f, mat: Mat): Mat {

        val pt = PerspectiveTransformation()

        return pt.transform(mat, rect)
    }

    fun getScannedBitmap(
        bitmap: Bitmap?,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        x3: Float,
        y3: Float,
        x4: Float,
        y4: Float
    ): Bitmap? {
        val perspective = PerspectiveTransformation()
        val rectangle = MatOfPoint2f()
        rectangle.fromArray(
            Point(x1.toDouble(), y1.toDouble()), Point(
                x2.toDouble(), y2.toDouble()
            ), Point(x3.toDouble(), y3.toDouble()), Point(
                x4.toDouble(),
                y4.toDouble()
            )
        )
        val dstMat = perspective.transform(DBImageUtils.bitmapToMat(bitmap), rectangle)
        return DBImageUtils.matToBitmap(dstMat)
    }

//    fun getCroppedBitmap(
//            bitmap: Bitmap?,
//            x1: Float,
//            y1: Float,
//            x2: Float,
//            y2: Float,
//            x3: Float,
//            y3: Float,
//            x4: Float,
//            y4: Float
//    ): Bitmap? {
//
//        val rect = Rect(
//                Point(x1.toDouble(),y1.toDouble()),
//                Point(x4.toDouble(), y4.toDouble())
//        )
//        val dstMat = Mat(DBImageUtils.bitmapToMat(bitmap), rect)
//
//        return DBImageUtils.matToBitmap(dstMat)
//    }

    fun cropBitmap(
            bitmap: Bitmap,
            point1: Point,
            point2: Point): Bitmap? {

        val rect = Rect(
                point1,
                point2
        )

        val dstMat = Mat(DBImageUtils.bitmapToMat(bitmap), rect)

        val croppedBitmap = DBImageUtils.matToBitmap(dstMat)

        dstMat.release()

        return croppedBitmap
    }

    private val areaDescendingComparator: Comparator<MatOfPoint2f?> = Comparator { o1, o2 ->
        val area1 = Imgproc.contourArea(o1)

        val area2 = Imgproc.contourArea(o2)

        ceil(area2 - area1).toInt()
    }

    private fun isQuadrilateral(polygon: MatOfPoint2f, srcArea: Int): Boolean {

        if (polygon.rows() != 4) {

            return false
        }

        val area = abs(Imgproc.contourArea(polygon))

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

        val ratio = DOWNSCALE_IMAGE_SIZE / srcMat.width().coerceAtLeast(srcMat.height())

        val downscaledSize = Size(
            (srcMat.width() * ratio).toDouble(),
            (srcMat.height() * ratio).toDouble()
        )

        var downscaled = Mat(downscaledSize, srcMat.type())

        Imgproc.resize(srcMat, downscaled, downscaledSize)

        downscaled= runGaussianBlur(downscaled)

        downscaled = runCannyEdge(downscaled)

        downscaled = runDilation(downscaled)

        val rectangles = runFindContours(downscaled)

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

    fun runMorphological(srcMat: Mat): Mat {

        val element = Imgproc.getStructuringElement(
            Imgproc.MORPH_RECT, Size(
                (2 * DILATE_ERODE_SIZE + 1).toDouble(),
                (2 * DILATE_ERODE_SIZE + 1).toDouble()
            )
        )

        var destMat = Mat()


        Imgproc.erode(srcMat, destMat, element)

        Imgproc.dilate(destMat, destMat, element)

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

    private fun runAdaptiveThreshold(
            srcMat: Mat,
            blockSize: Int,
            meanOffset: Double): Mat {

        var destMat = Mat()

        Imgproc.adaptiveThreshold(
            srcMat,
            destMat,
            255.0,
            Imgproc.ADAPTIVE_THRESH_MEAN_C,//Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,//
            Imgproc.THRESH_BINARY,
            blockSize, meanOffset
        )

        return destMat
    }

    fun runAdaptiveThresholdBitmap(
            bitmap: Bitmap,
            blockSize: Int,
            meanOffset: Double): Bitmap {

        var srcMat = DBImageUtils.bitmapToMat(bitmap)

        srcMat = runGreyScale(srcMat)

        srcMat = runAdaptiveThreshold(srcMat, blockSize, meanOffset)

        val tmpBitmap = DBImageUtils.matToBitmap(srcMat)

        srcMat.release()

        return tmpBitmap
    }

    private fun runOTSUsThreshold(srcMat: Mat, min: Double, max: Double): Mat {

        var dstMat = Mat()

        Imgproc.threshold(
            srcMat,
            dstMat,
            min, // threshold value, ignored when using cv2.THRESH_OTSU
            max, // maximum value assigned to pixel values exceeding the threshold
            Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
        ) // thresholding type

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

    // Dilate Canny output to remove potential holes between edge segments.
    private fun runDilation(srcMat: Mat): Mat {

        val tmpMat = Mat()

        Imgproc.dilate(srcMat, tmpMat, Mat.ones(Size(3.0, 3.0), 0))

        return tmpMat
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

            if (isQuadrilateral(approx, srcArea)) { // rectangle found

                rectangles.add(approx)

                break
            }

            mop2f.release()
        }

        hierarchy.release()

        return rectangles
    }

    /** Used by intern fragment for processing single bitmap image */
    private fun getPointsFor(src: Mat): MutableList<MatOfPoint2f> {

        // Blur the image to filter out the noise.
        val blurred = Mat()
        Imgproc.medianBlur(src, blurred, 9)

        // Set up images to use.
        val gray0 = Mat(blurred.size(), CvType.CV_8U)
        val gray = Mat()

        // For Core.mixChannels.
        val contours: List<MatOfPoint> = ArrayList()
        val rectangles: MutableList<MatOfPoint2f> = ArrayList()

        val sources: MutableList<Mat> = ArrayList()
        sources.add(blurred)
        val destinations: MutableList<Mat> = ArrayList()
        destinations.add(gray0)

        // To filter rectangles by their areas.
        val srcArea: Int = src.rows() * src.cols()

        // Find squares in every color plane of the image.
        for (c in 0..2) {
            val ch = intArrayOf(c, 0)
            val fromTo = MatOfInt(*ch)
            Core.mixChannels(sources, destinations, fromTo)

            // Try several threshold levels.
            for (l in 0 until 2) {
                if (l == 0) {
                    // HACK: Use Canny instead of zero threshold level.
                    // Canny helps to catch squares with gradient shading.
                    // NOTE: No kernel size parameters on Java API.
                    Imgproc.Canny(gray0, gray, 10.0, 20.0)

                    // Dilate Canny output to remove potential holes between edge segments.
                    Imgproc.dilate(gray, gray, Mat.ones(Size(3.0, 3.0), 0))
                } else {
                    val threshold = (l + 1) * 255 / 2
                    Imgproc.threshold(
                        gray0,
                        gray,
                        threshold.toDouble(),
                        255.0,
                        Imgproc.THRESH_BINARY
                    )
                }

                // Find contours and store them all as a list.
                Imgproc.findContours(
                    gray,
                    contours,
                    Mat(),
                    Imgproc.RETR_LIST,
                    Imgproc.CHAIN_APPROX_SIMPLE
                )
                for (contour in contours) {
                    val contourFloat = DBMathUtils.toMatOfPointFloat(contour)
                    val arcLen = Imgproc.arcLength(contourFloat, true) * 0.02

                    // Approximate polygonal curves.
                    val approx = MatOfPoint2f()
                    Imgproc.approxPolyDP(contourFloat, approx, arcLen, true)
                    if (isQuadrilateral(approx, srcArea)) {
                        rectangles.add(approx)
                    }
                }
            }
        }

        return rectangles
    }

    /** Used by intern fragment for processing single bitmap image */
    fun processImageForPoints(bitmap: Bitmap?): MatOfPoint2f? {

        val src = DBImageUtils.bitmapToMat(bitmap)

        // Downscale image for better performance.
        val ratio = DOWNSCALE_IMAGE_SIZE / src.width().coerceAtLeast(src.height())
        val downscaledSize = Size(
            (src.width() * ratio).toDouble(),
            (src.height() * ratio).toDouble()
        )
        val downscaled = Mat(downscaledSize, src.type())
        Imgproc.resize(src, downscaled, downscaledSize)
        val rectangles: List<MatOfPoint2f> = getPointsFor(downscaled)
        if (rectangles.isEmpty()) {
            return null
        }
        Collections.sort(rectangles, areaDescendingComparator)
        val largestRectangle = rectangles[0]
        return DBMathUtils.scaleRectangle(largestRectangle, (1f / ratio).toDouble())
    }

}