package co.deshbidesh.db_android.db_document_scanner_feature.model

import android.graphics.Bitmap

data class DBDocScanImage(
    var bitmap: Bitmap,
    var orientation: Int
)

/*
object CoroutinesTaskManager {

    fun doTask(bitmap: Bitmap, orientation: Int, work: suspend (DBDocScanImage?) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {

            work(
                DBImageUtils.rotateByOrientation(
                    bitmap,
                    orientation
                )
            )
        }
    }

    fun uriToBitmap(uri: Uri, resolver: ContentResolver, work: suspend ((Bitmap?) -> Unit)) {

        CoroutineScope(Dispatchers.IO).launch {

            work(
                decodeBitmap(
                    uri,
                    resolver
                )
            )
        }
    }

    fun processImageForPoints(bitmap: Bitmap, helper: OpenCVNativeHelper, work: suspend (List<PointF>) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {

            val result: MutableList<PointF> = ArrayList()

            val matOfPoint2f = helper.processImageForPoints(bitmap)

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

    private fun decodeBitmap(uri: Uri, resolver: ContentResolver): Bitmap {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))

        } else {

            MediaStore.Images.Media.getBitmap(resolver, uri)
        }
    }
}

 */