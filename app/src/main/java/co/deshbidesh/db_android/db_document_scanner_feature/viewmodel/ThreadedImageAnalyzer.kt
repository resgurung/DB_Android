package co.deshbidesh.db_android.db_document_scanner_feature.viewmodel

import android.os.Handler
import androidx.camera.core.ImageAnalysis


interface DBHandler {

    fun getHandler(): Handler
}

interface ThreadedImageAnalyzer:DBHandler,  ImageAnalysis.Analyzer {}