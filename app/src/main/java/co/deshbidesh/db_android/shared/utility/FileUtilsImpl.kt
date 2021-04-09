package co.deshbidesh.db_android.shared.utility

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import co.deshbidesh.db_android.R
import java.io.File

class FileUtilsImpl(
         private val activity: Activity
         )
     : FileUtils {

     companion object {

         const val androidMinSDK = 29
     }

    override fun createDirectoryIfNotExist(): File {

      val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {

        File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() } }

      return if (mediaDir != null && mediaDir.exists())

        mediaDir else activity.filesDir
    }

    override fun makeFile(outputDirectory: File, filename: String): File {

      return File(
            outputDirectory,
            filename)
    }


   override fun refreshGallery(file: File) {

     if( android.os.Build.VERSION.SDK_INT >= androidMinSDK ) {

       MediaScannerConnection.scanFile(activity, arrayOf(file.toString()),
               arrayOf(file.name), null)

     } else {

       val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

       mediaScanIntent.data = Uri.fromFile(file)

       activity.sendBroadcast(mediaScanIntent)
     }
   }
 }