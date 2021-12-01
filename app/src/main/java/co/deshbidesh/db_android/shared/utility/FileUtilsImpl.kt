package co.deshbidesh.db_android.shared.utility

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.db_note_feature.note_utils.NotesImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileUtilsImpl(
    private val activity: Activity
) : FileUtils {

    companion object {

        const val androidMinSDK = 29

        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        const val IMAGE_QUALITY = 90

        const val EXTENSION = ".jpg"
    }

    override fun createDirectoryIfNotExist(): File {

        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {

            File(it, activity.resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists())

            mediaDir else activity.filesDir
    }

    override fun makeFile(outputDirectory: File, filename: String): File {

        return File(
            outputDirectory,
            filename
        )
    }

    override fun refreshGallery(file: File) {

        if (android.os.Build.VERSION.SDK_INT >= androidMinSDK) {

            MediaScannerConnection.scanFile(
                activity, arrayOf(file.toString()),
                arrayOf(file.name), null
            )

        } else {

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

            mediaScanIntent.data = Uri.fromFile(file)

            activity.sendBroadcast(mediaScanIntent)
        }
    }

    override fun deleteFile(path: String) {

        val file = File(path)

        if (file.exists()) {

            file.delete()

            refreshGallery(file)
        }
    }

    override fun writeImageToExternalStorage(
        file: File,
        bitmap: Bitmap) {

        val outputStream: OutputStream = FileOutputStream(file)

        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            IMAGE_QUALITY,
            outputStream) // 90 is optimal

        outputStream.flush()

        outputStream.close()
    }

    override fun preparePathForTempImage(
        uri: Uri
    ): Pair<Bitmap?, File?> {

        val inStream: InputStream? = activity.contentResolver.openInputStream(uri)

        val selectedImg: Bitmap = BitmapFactory.decodeStream(inStream)

        val file = createTempFiles()

        inStream?.close() // idempotent

        return Pair(selectedImg, file)
    }

    /*
        Creates a temp files in the internal cache.
        Although android may clear the cache when it need the space,
        for good house keeping remove the cache after we have finish with it.
     */
    private fun createTempFiles(): File {

        val outputDir = activity.cacheDir // context being the Activity pointer

        val format = SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.UK
        ).format(
            System.currentTimeMillis()
        )

        return File.createTempFile(format, EXTENSION, outputDir)
    }
}