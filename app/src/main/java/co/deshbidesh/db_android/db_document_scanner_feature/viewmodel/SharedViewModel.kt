package com.robin.cameraxtutorial.camerax.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.ArOverlayView
import co.deshbidesh.db_android.db_document_scanner_feature.processor.OpenCVNativeHelper

class SharedViewModel: ViewModel() {

    private val mutableArOverlayView = MutableLiveData<ArOverlayView>()

    val arOverlayView: LiveData<ArOverlayView> = mutableArOverlayView

    private val mutableBitmapData = MutableLiveData<Bitmap>()

    val bitmapdata: LiveData<Bitmap> = mutableBitmapData

    val opencvHelper: OpenCVNativeHelper by lazy { OpenCVNativeHelper() }

    fun addData(bitmap: Bitmap) {

        mutableBitmapData.postValue(bitmap)
    }
}