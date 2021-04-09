package co.deshbidesh.db_android.shared.utility

import java.io.File

interface FileUtils {

  fun createDirectoryIfNotExist(): File

  fun makeFile(outputDirectory: File, filename: String): File

  fun refreshGallery(file: File)
}