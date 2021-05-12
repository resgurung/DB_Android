package co.deshbidesh.db_android.db_document_scanner_feature.viewmodel

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
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBScannedObjectInfo
import co.deshbidesh.db_android.db_document_scanner_feature.model.EdgePoint
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.ArObject
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.ArObjectTracker
import co.deshbidesh.db_android.db_document_scanner_feature.processor.OpenCVNativeHelper
import co.deshbidesh.db_android.shared.extensions.mapOrientation
import co.deshbidesh.db_android.shared.extensions.yuvToRgba
import java.util.concurrent.atomic.AtomicBoolean


class DocumentAnalyzer(
        private val opencvHelper: OpenCVNativeHelper
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

                        val size = Size(rgbaMat.width(), rgbaMat.height())

                        val rotatedMat = opencvHelper.runRotate(rgbaMat, mapOrientation(rotation))

                        val greyMat = opencvHelper.runGreyScale(rotatedMat)

                        var tmpRectF = RectF()

                        var points = arrayListOf<EdgePoint>()

                        val matOfPoint2f = opencvHelper.getPointsFromMat(greyMat)

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
                            }

                        }

                        uiHandler.post {

                            arObjectTracker.processObject(
                                    ArObject(
                                            trackingId = 18,
                                            boundingBox = tmpRectF,
                                            sourceSize = size,
                                            objectInfo = DBScannedObjectInfo(points, rotation, rotatedMat)
                                    )
                            )
                        }

                        isBusy.set(false)

                        rgbaMat.release()

                        greyMat.release()

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
}