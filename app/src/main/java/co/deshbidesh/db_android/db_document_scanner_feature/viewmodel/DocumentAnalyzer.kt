package com.robin.cameraxtutorial.camerax.viewmodel

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.graphics.RectF
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.util.Size
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import com.robin.cameraxtutorial.camerax.ar.ArObject
import com.robin.cameraxtutorial.camerax.ar.ArObjectTracker
import com.robin.cameraxtutorial.documentscanner.OpenCVNativeHelper
import com.robin.cameraxtutorial.extensions.yuvToRgba
import com.robin.cameraxtutorial.helper.EdgePoint
import org.opencv.core.Core
import org.opencv.core.MatOfPoint2f
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean


/** Helper type alias used for analysis use case callbacks */

typealias MatOfPointListener = (matofPoint2f: MatOfPoint2f) -> Unit

class DocumentAnalyzer(
    private val opencvHelper: OpenCVNativeHelper,
   // listener: MatOfPointListener
    ): ViewModel(), ThreadedImageAnalyzer {

    companion object {

        const val TAG = "DocumentAnalyzer"
    }

    val arObjectTracker = ArObjectTracker()
    private val isBusy = AtomicBoolean(false)
    private val handlerThread = HandlerThread("DocumentAnalyzer").apply { start() }
    private val uiHandler = Handler(Looper.getMainLooper())

    /** */
    override fun getHandler() = Handler(handlerThread.looper)

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(image: ImageProxy) {

        try {

            image.image?.let { currentImage ->

                if (isBusy.compareAndSet(false, true)) {

                    if (currentImage.format == ImageFormat.YUV_420_888
                        && currentImage.planes.size == 3) {

                        val rotation = image.imageInfo.rotationDegrees

                        val rgbaMat = currentImage.yuvToRgba()

                        val greyMat = opencvHelper.runGreyScale(rgbaMat)

                        val rotatedMat = opencvHelper.runRotate(greyMat, mapOrientation(rotation))

                        val size = Size(rotatedMat.width(), rotatedMat.height())

                        var tmpRectF = RectF()

                        var points = arrayListOf<EdgePoint>()

                        val matOfPoint2f = opencvHelper.getPointsFromMat(rotatedMat)

                        if (matOfPoint2f != null) {

                            val boundingRect = opencvHelper.getRectFrom(matOfPoint2f)

                            tmpRectF = RectF(boundingRect.x.toFloat(),
                                    boundingRect.y.toFloat(),
                                    (boundingRect.x + boundingRect.width).toFloat(),
                                    (boundingRect.y + boundingRect.height).toFloat())

                            val openCVPoints = matOfPoint2f.toArray()

                            if (openCVPoints.isNotEmpty()) {

                                for(point in openCVPoints) {

                                    points.add(EdgePoint(point.x, point.y))
                                }

                                Log.d(TAG, "Edge Points: $points")
                            }

                        }

                        Log.d(TAG, "Rect: $tmpRectF")
                        Log.d(TAG, "Size: $size")

                        uiHandler.post {

                            arObjectTracker.processObject(
                                    ArObject(
                                            trackingId = 18,
                                            boundingBox = tmpRectF,
                                            sourceSize = size,
                                            sourceRotationDegrees = 0,
                                            points = points
                                    )
                            )
                        }

                        isBusy.set(false)

                        rgbaMat.release()

                        greyMat.release()

                        rotatedMat.release()

                    } else {
                        // Manage other image formats
                        Log.d("DocumentAnalyzer", "Different format")
                    }
                } else {

                    Log.d("DocumentAnalyzer", "Engine Busy")
                }
            }

        } catch (e: Exception) {

            Log.e("DocumentAnalyzer", "${e.printStackTrace()}")
        }

        image.close()
    }

    private fun mapOrientation(degree: Int): Int {
        return when (degree) {
            0 -> Core.ROTATE_180
            90 -> Core.ROTATE_90_CLOCKWISE
            180 -> Core.ROTATE_180
            270 -> Core.ROTATE_90_COUNTERCLOCKWISE
            else -> Core.ROTATE_90_CLOCKWISE
        }
    }

}