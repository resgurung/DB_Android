package co.deshbidesh.db_android.db_document_scanner_feature.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.graphics.*
import android.media.Image
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils.DBImageUtils
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanImage
import co.deshbidesh.db_android.db_document_scanner_feature.model.EdgePoint
import co.deshbidesh.db_android.db_document_scanner_feature.processor.OpenCVNativeHelper
import co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment.DBDocScanInternFragment
import co.deshbidesh.db_android.shared.extensions.imageToBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.core.Point
import java.util.*


class SharedViewModel: ViewModel() {


    enum class Route {

        SELECTION_FRAGMENT,

        CAMERA_FRAGMENT,

        INTERN_FRAGMENT,

        RESULT_FRAGMENT
    }

    private var currentRoute: Route = Route.SELECTION_FRAGMENT

    val opencvHelper: OpenCVNativeHelper by lazy { OpenCVNativeHelper() }

    var uri: Uri? = null

    var libraryLoaded: Boolean = false

    ///////////////////////////////////////////////////////

    fun scaledBitmap(
            bitmap: Bitmap,
            width: Int,
            height: Int
    ): Bitmap {

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
                m,
                true)
    }

    //////////////////////////////////////////////////////

    // use cases
    // 1st use case (Selection -> Intern -> Result)
    // selection fragment navigates to Intern fragment after choosing image by passing URI
    // intern fragment extracts image from URI and puts it on 0th position of the array
    // intern fragment uses the copy of the 0th position bitmap and modifies
    // intern fragment navigates to result fragment by adding the modified bitmap to the 1st position
    // Back press from result fragment nullify the 1st position bitmap in the array and displays the 0th position bitmap
    // Back press from intern fragment goes to selection fragment and nullify 0th element bitmap


    // 2nd use case (Camera -> Result)
    // camera fragment adds bitmap on the 0th position
    // back press from result fragment nullify the 0th element and shows camera fragment


    // 3rd use case (camera -> Intern -> Result)
    // camera fragment adds bitmap on the 0th position and navigate to intern fragment
    // intern fragment modifies the copy of the 0th position bitmap
    // intern adds bitmap on the 1st position and navigates to result fragment
    // back press on result fragment goes to intern and uses bitmap on the 0st position as well as removes the 1st position bitmap
    // back press on intern fragment goes to camera fragment and removes the 0th element of the bitmap array
    private val bitmapArray = arrayOfNulls<Bitmap>(2)

    fun insertInFirst(bitmap: Bitmap) {

        bitmapArray[0] = bitmap
    }

    fun insertInSecond(bitmap: Bitmap) {
        bitmapArray[1] = bitmap
    }

    fun getFirstBitmap(): Bitmap? {

        return bitmapArray[0]
    }

    fun getSecondBitmap(): Bitmap? {

        return bitmapArray[1]
    }

    fun clearFirst() {

        if (bitmapArray[0] != null) {

            bitmapArray[0] = null
        }
    }

    fun clearSecond() {

        if (bitmapArray[1] != null) {

            bitmapArray[1] = null
        }
    }

    //////////////////////////////  Route //////////////////

    fun getRoute(): Route = currentRoute

    fun setRoute(route: Route) {

        currentRoute = route
    }


    /////////////////////////////////// Coroutines /////////////////////
    fun rotateByOrientation(
            bitmap: Bitmap,
            orientation: Int,
            listener: suspend (DBDocScanImage?) -> Unit
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            listener(
                    DBImageUtils.rotateByOrientation(
                            bitmap,
                            orientation
                    )
            )
        }
    }

    fun uriToBitmap(
            context: Context,
            listener: suspend ((Bitmap?, String) -> Unit)
    ) {

        uri?.let {

            viewModelScope.launch(Dispatchers.IO)  {

                val bitmap = bitmapDecoder(it, context.contentResolver)

                val absolutePath = DBImageUtils.getAbsolutePathFor(context, it)

                listener(bitmap, absolutePath)
            }
        }

    }

    private fun bitmapDecoder(
            uri: Uri,
            resolver: ContentResolver
    ): Bitmap {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))

        } else {

            MediaStore.Images.Media.getBitmap(resolver, uri)
        }
    }

    fun processImageForPoints(
            bitmap: Bitmap,
            work: suspend (List<PointF>) -> Unit
    ) {

        viewModelScope.launch(Dispatchers.IO)  {

            val result: MutableList<PointF> = ArrayList()

            val matOfPoint2f = opencvHelper.processImageForPoints(bitmap)

            if (matOfPoint2f != null) {
                val points = listOf(*matOfPoint2f.toArray())

                for (i in points.indices) {
                    Log.d(DBDocScanInternFragment.TAG,"Point: $i")
                    result.add(PointF(points[i].x.toFloat(), points[i].y.toFloat()))
                }
            }
            work(result)
        }
    }
    ///////////////////////////////////////////////////////////////////////////////

    fun rotateImageIfRequired(
            absPath: String,
            bitmap: Bitmap
    ): DBDocScanImage? {

        return DBImageUtils.rotateBitmapWith(absPath, bitmap)
    }

    /////////////////////////////////// DBDocScanResultProcessFragment ///////////////////

    fun convertRGBToGrey(
            bitmap: Bitmap,
            listener: suspend (Bitmap?) -> Unit
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            val greyBitmap = opencvHelper.runGreyScaleBitmap(bitmap)

            listener(greyBitmap)

        }
    }

    fun convertRGBToBW(
            bitmap: Bitmap,
            blockValue: Int,
            offset: Double,
            listener: suspend (Bitmap?) -> Unit
    ){

        convertRGBToGrey(bitmap) { greyBitmap ->

            greyBitmap?.let {

                val bw = opencvHelper.runAdaptiveThresholdBitmap(it, blockValue, offset)

                listener(bw)
            }
        }
    }

    fun cropImage(
            bitmap: Bitmap?,
            size: Size,
            points: Pair<PointF,PointF>,
            listener: (Bitmap?) -> Unit
    ) {


        bitmap?.let {

            viewModelScope.launch(Dispatchers.IO) {

                val w = it.width.toDouble()

                val h = it.height.toDouble()

                val xRatio: Double = w / size.width
                val yRatio: Double = h / size.height

                val x1 = points.first.x * xRatio //points[0].x * xRatio
                val y1 = points.first.y * yRatio //points[0].y * yRatio

                val x4 = points.second.x * xRatio //points[3].x * xRatio
                val y4 = points.second.y * yRatio //points[3].y * yRatio

                val croppedBitmap = opencvHelper.cropBitmap(
                        it,
                        Point(x1, y1),
                        Point(x4, y4)
                )

                listener(croppedBitmap)
            }
        } ?: run {

            listener(null)
        }

    }

    /////////////////////  DBDocScanCameraFragment ////////////////////////

    fun processImage(
            image: Image,
            points: List<EdgePoint>,
            listener: (Route) -> Unit
    ) {

        if (points.isEmpty()) {

            listener(routeToIntern(image))

        } else {

            val bitmap = opencvHelper.getScannedBitmap(image, points)

            if (bitmap == null) {

                insertInFirst(image.imageToBitmap())

                listener(Route.INTERN_FRAGMENT)

            } else {

                insertInFirst(bitmap)

                listener(Route.RESULT_FRAGMENT)
            }
        }
    }

    private fun routeToIntern(
            image: Image
    ): Route {

        insertInFirst(image.imageToBitmap())

        return Route.INTERN_FRAGMENT
    }

    private fun routeToResult(
            bitmap: Bitmap
    ): Route {

        insertInFirst(bitmap)

        return Route.RESULT_FRAGMENT
    }

    ///////////////////////////////////////////////////

}