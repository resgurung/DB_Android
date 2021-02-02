package co.deshbidesh.db_android.db_document_scanner_feature.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.deshbidesh.db_android.db_document_scanner_feature.processor.OpenCVNativeHelper
import com.robin.cameraxtutorial.camerax.viewmodel.DocumentAnalyzer

class DocumentAnalyzerViewModelFactory(private val helper: OpenCVNativeHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DocumentAnalyzer::class.java)) {

            return DocumentAnalyzer(helper) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}