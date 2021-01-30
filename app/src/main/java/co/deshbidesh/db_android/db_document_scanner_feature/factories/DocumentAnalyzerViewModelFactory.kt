package com.robin.cameraxtutorial.camerax.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robin.cameraxtutorial.documentscanner.OpenCVNativeHelper

class DocumentAnalyzerViewModelFactory(private val helper: OpenCVNativeHelper): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DocumentAnalyzer::class.java)) {

            return DocumentAnalyzer(helper) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}