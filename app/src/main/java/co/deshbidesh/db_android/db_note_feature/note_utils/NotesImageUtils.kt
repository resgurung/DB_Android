package co.deshbidesh.db_android.db_note_feature.note_utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import co.deshbidesh.db_android.shared.utility.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class NotesImageUtils {

    companion object{

        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        const val IMAGE_QUALITY = 90

        const val EXTENSION = ".jpg"

        ///////////////////////////
        @JvmStatic
        fun preparePathForImage(
            path: String,
            fileUtils: FileUtils,
            context: Context
        ): Pair<Bitmap?, File?> {

            val imgUri: Uri = Uri.parse(path)

            var inStream: InputStream? = context.contentResolver.openInputStream(imgUri)

            val selectedImg: Bitmap = BitmapFactory.decodeStream(inStream)

            val dir = fileUtils.createDirectoryIfNotExist()

            val file = fileUtils.makeFile(
                dir,
                SimpleDateFormat(
                    NotesImageUtils.FILENAME_FORMAT,
                    Locale.UK
                ).format(
                    System.currentTimeMillis()
                ) + EXTENSION
            )

            inStream?.close() // idempotent

            return Pair(selectedImg, file)
        }

        @JvmStatic
        fun preparePathForTempImage(
            uri: Uri,
            context: Context
        ): Pair<Bitmap?, File?> {

            val inStream: InputStream? = context.contentResolver.openInputStream(uri)

            val selectedImg: Bitmap = BitmapFactory.decodeStream(inStream)

            val file = createTempFiles(context)

            inStream?.close() // idempotent

            return Pair(selectedImg, file)
        }

        @JvmStatic
        fun writeImageToExternalStorage(file: File, bitmap: Bitmap) {

            val outputStream: OutputStream = FileOutputStream(file)

            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                IMAGE_QUALITY,
                outputStream) // 90 is optimal

            outputStream.flush()

            outputStream.close()
        }

        /*
        Creates a temp files in the internal cache.
        Although android may clear the cache when it need the space,
        for good house keeping remove the cache after we have finish with it.
         */
        @JvmStatic
        fun createTempFiles(context: Context): File {

            val outputDir = context.cacheDir // context being the Activity pointer

            val format = SimpleDateFormat(
                FILENAME_FORMAT,
                Locale.UK
            ).format(
                System.currentTimeMillis()
            )

            return File.createTempFile(format, EXTENSION, outputDir)
        }

        ////////////////////////////////////

        @JvmStatic
        fun prepareFilePath(imageList: ArrayList<String>,
                            destFilePath: ArrayList<String>,
                            bitMapFileMap: HashMap<Bitmap, File>,
                            context:Context,
                            fileUtils: FileUtils){

            val dir  = fileUtils.createDirectoryIfNotExist()

            var inStream: InputStream? = null

            for (path in imageList) {
                // Decode bitmap from uri
                val imgUri: Uri? = Uri.parse(path)

                imgUri?.let {

                    inStream = context.contentResolver?.openInputStream(it)

                    inStream?.let {
                        val selectedImg: Bitmap = BitmapFactory.decodeStream(inStream)

                        // Determine destination file path
                        //val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                        //val dirPath = activity.getExternalFilesDir("/images/")?.absolutePath
                        //val file = File(dirPath, "img-${timeStamp}.jpg")
                        val file = fileUtils.makeFile(
                            dir,
                            SimpleDateFormat(
                                FILENAME_FORMAT,
                                Locale.UK
                            ).format(
                                System.currentTimeMillis()
                            ) + ".jpg"
                        )
                        val finalFilePath = file.path
                        destFilePath.add(finalFilePath)
                        bitMapFileMap.put(selectedImg, file)
                    }
                }

            }

            inStream?.close() // idempotent
            if(inStream != null)
                inStream = null
        }

        @JvmStatic
        fun writeImagesToExternalStorage(fileMap: HashMap<Bitmap, File>) {
            var outputStream: OutputStream? = null;
            fileMap.forEach {
                outputStream = FileOutputStream(it.value)
                it.key.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    outputStream
                )
            }

            outputStream?.flush()

            outputStream?.close()   // idempotent
            if(outputStream != null)
                outputStream = null
        }


        @JvmStatic
        fun createImageFile(activity: Activity, cameraImgList:ArrayList<String>)
        : File? {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir: File? =  activity?.getExternalFilesDir("/images/")

            return File.createTempFile(
                "img-${timeStamp}",
                ".jpg",
                storageDir
            ).apply {
                val currentPhotoPath = absolutePath
                cameraImgList.add(currentPhotoPath)
            }
        }

        @JvmStatic
        fun deleteImagesFromStorage(list: ArrayList<String>) {
            for(path in list) {
                Log.d("NotesImageUtils", "Path: ---xx--- ${path} ---xx---")
                val file = File(path)
                file.delete();
            }
        }
    }
}