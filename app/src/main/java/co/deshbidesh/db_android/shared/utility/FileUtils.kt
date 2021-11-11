package co.deshbidesh.db_android.shared.utility

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface FileUtils {

    /*
      creates dir with "DeshBidesh" dir if it is not created yet
      if already exists then it return the dir
     */
    fun createDirectoryIfNotExist(): File

    /*
      make file with the dir and file name
     */
    fun makeFile(outputDirectory: File, filename: String): File

    /*
      After file delete refresh the gallery with the file name
     */
    fun refreshGallery(file: File)

    /*
      deletes file with the file path
    */
    fun deleteFile(path: String)

    /*
      writes files to the storage
    */
    fun writeImageToExternalStorage(file: File, bitmap: Bitmap)

    /*
      Prepares File and Bitmap from Uri which is stored in the Pair
      Both return type are optional
     */
    fun preparePathForTempImage(
      uri: Uri
    ): Pair<Bitmap?, File?>
}