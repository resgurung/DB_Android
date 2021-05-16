package co.deshbidesh.db_android.db_note_feature.note_utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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

        @JvmStatic
        fun prepareFilePath(imageList: ArrayList<String>,
                            destFilePath: ArrayList<String>,
                            bitMapFileMap: HashMap<Bitmap, File>,
                            context:Context,
                            activity: Activity){

            var inStream: InputStream? = null

            for (path in imageList) {
                // Decode bitmap from uri
                val imgUri: Uri? = Uri.parse(path)
                inStream = context?.contentResolver?.openInputStream(imgUri!!)

                inStream?.let {
                    val selectedImg: Bitmap = BitmapFactory.decodeStream(inStream)

                    // Determine destination file path
                    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val dirPath = activity.getExternalFilesDir("/images/")?.absolutePath
                    val file = File(dirPath, "img-${timeStamp}.jpg")
                    val finalFilePath = file.path
                    destFilePath.add(finalFilePath)
                    bitMapFileMap.put(selectedImg, file)
                }
            }

            inStream?.close() // idempotent
            if(inStream != null)
                inStream = null;
        }

        @JvmStatic
        fun writeImagesToExternalStorage(fileMap: HashMap<Bitmap, File>) {
            var outputStream: OutputStream? = null;
            fileMap.forEach {
                outputStream = FileOutputStream(it.value)
                it.key.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

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
                val file = File(path)
                file.delete();
            }
        }
    }
}