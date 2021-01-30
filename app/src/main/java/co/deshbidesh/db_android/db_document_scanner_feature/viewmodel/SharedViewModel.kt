package com.robin.cameraxtutorial.camerax.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.robin.cameraxtutorial.camerax.ar.ArOverlayView
import com.robin.cameraxtutorial.documentscanner.OpenCVNativeHelper

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