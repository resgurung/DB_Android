package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.annotation.SuppressLint
import co.deshbidesh.db_android.R
import android.app.AlertDialog
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.BuildConfig
import co.deshbidesh.db_android.databinding.FragmentDBDocScanCameraBinding
import co.deshbidesh.db_android.db_document_scanner_feature.model.EdgePoint
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.*
import co.deshbidesh.db_android.shared.extensions.imageToBitmap
import com.robin.cameraxtutorial.camerax.viewmodel.DocumentAnalyzer
import co.deshbidesh.db_android.db_document_scanner_feature.factories.DocumentAnalyzerViewModelFactory
import com.robin.cameraxtutorial.camerax.viewmodel.SharedViewModel
import com.robin.cameraxtutorial.camerax.viewmodel.ThreadedImageAnalyzer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class DBDocScanCameraFragment : Fragment() {

    companion object{

        val TAG = DBDocScanCameraFragment::class.simpleName

        const val NUMBER_OF_TIMES_LAST_POINTS_TO_BE_USED = 4

        val targetResolution: Size = Size(720, 1280)
    }

    object Coroutines {

        fun uriToBitmap(uri: Uri, resolver: ContentResolver, work: suspend ((Bitmap?) -> Unit)) {

            CoroutineScope(Dispatchers.IO).launch {

                work(decodeBitmap(uri, resolver))
            }
        }

        private fun decodeBitmap(uri: Uri, resolver: ContentResolver): Bitmap {

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri))

            } else {

                MediaStore.Images.Media.getBitmap(resolver, uri)
            }
        }
    }

    private val loader = object: BaseLoaderCallback(context) {

        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)

            when (status) {

                LoaderCallbackInterface.SUCCESS -> {

                    startCamera()
                }
                else -> {

                    super.onManagerConnected(status)
                }
            }
        }
    }

    private var _binding: FragmentDBDocScanCameraBinding? = null

    private val binding get() = _binding!!

    private val mutableArOverlayView = MutableLiveData<ArOverlayView>()

    private val arOverlayView: LiveData<ArOverlayView> = mutableArOverlayView

    var cameraRunning = false
        private set

    private var imageAnalyzer: ThreadedImageAnalyzer? = null
        set(value) {
            field = value
            if (cameraRunning) {
                startCamera()
            }
        }

    private var edgePoints: List<EdgePoint>? = null

    private lateinit var viewModelFactory: DocumentAnalyzerViewModelFactory

    private lateinit var imageAnalyzerViewModel: DocumentAnalyzer

    private var imageCapture: ImageCapture? = null

    private lateinit var animator: BoundingBoxAnimator

    // Select back camera as a default. We will only use back camera
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private val cameraProviderFuture by lazy { context?.let { ProcessCameraProvider.getInstance(it) } }

    private val executor: Executor by lazy { Executors.newSingleThreadExecutor() }

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDBDocScanCameraBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mutableArOverlayView.postValue(binding.arOverlays) // background thread

        animator = BoundingBoxAnimator(NUMBER_OF_TIMES_LAST_POINTS_TO_BE_USED) { points ->

            edgePoints = points
        }

        context?.let { currContext ->

            viewModelFactory = DocumentAnalyzerViewModelFactory(sharedViewModel.opencvHelper)

            imageAnalyzerViewModel = ViewModelProvider(this, viewModelFactory).get(DocumentAnalyzer::class.java)

            imageAnalyzer = imageAnalyzerViewModel

            val boundingBoxArOverlay = BoundingBoxArOverlay(currContext, BuildConfig.DEBUG)

            arOverlayView.observe(viewLifecycleOwner) {

                it.doOnLayout { view ->

                    imageAnalyzerViewModel.arObjectTracker
                        .pipe(animator)
                        .pipe(PositionTranslator(view.width, view.height))
                        .pipe(PathInterpolator())
                        .addTrackingListener(boundingBoxArOverlay)
                }

                it.add(boundingBoxArOverlay)
            }
        }

        binding.cameraCaptureButton.setOnClickListener {

            saveImageToMemory()
        }
    }

    override fun onResume() {
        super.onResume()

        loadOpenCVLibrary()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (cameraRunning) {

            cameraProviderFuture?.get()?.unbindAll()

            cameraRunning = false

            Log.i("CameraFragment", "Stopping camera")
        }

        _binding = null
    }

    private fun loadOpenCVLibrary() {

        if (!OpenCVLoader.initDebug()) {

            Log.e(TAG, "Failed to load OpenCV lib")

        } else {

            loader.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() = binding.preview.post {

        cameraProviderFuture?.addListener(Runnable {

            try {

                // Make sure that there are no other use cases bound to CameraX
                cameraProviderFuture?.get()?.unbindAll()

                // Create configuration object for the viewfinder use case
                val previewConfig = onCreatePreviewConfigBuilder()

                // setup image capture
                imageCapture = createCaptureUseCase()

                // Setup image analysis pipeline that computes average pixel luminance in real time

                val imageAnalysis = onCreateAnalyzerConfigBuilder()

                if (imageAnalyzer != null) {
                    imageAnalysis.setAnalyzer(executor, imageAnalyzer!!)
                }

                // Bind use cases to lifecycle
                cameraProviderFuture?.get()?.bindToLifecycle(
                    this, cameraSelector, previewConfig, imageCapture, imageAnalysis)

                // set surface provider
                previewConfig.setSurfaceProvider(binding.preview.surfaceProvider)

                cameraRunning = true

                Log.i("CameraFragment", "Started camera")

            } catch (e: Exception) {

                Log.e("CameraFragment", "$e")

                showAlert("The camera is not responding. Please try again later.")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun saveImageToMemory() {

        imageCapture?.takePicture(executor,
            object : ImageCapture.OnImageCapturedCallback() {

                override fun onError(exception: ImageCaptureException) {

                    binding.preview.post {

                        showAlert("Photo capture failed: ${exception.message}")
                    }

                }

                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {

                    image.image?.let { currImage ->

                        edgePoints?.let { points ->

                            val scannedImage = sharedViewModel.opencvHelper.getScannedBitmap(currImage, points)

                            if (scannedImage != null) {

                                sharedViewModel.addData(scannedImage)

                                navigate(R.id.action_dbDocScanCameraFragment_to_DBDocScanResultFragment)

                            } else {

                                val bitmapImage = currImage.imageToBitmap()

                                sharedViewModel.addData(bitmapImage)

                                navigate(R.id.action_scan_cameraFragment_to_internFragment)
                            }
                        } ?: run {

                            val bitmapImage = currImage.imageToBitmap()

                            sharedViewModel.addData(bitmapImage)

                            navigate(R.id.action_scan_cameraFragment_to_internFragment)
                        }

                    } ?: run {

                        showAlert("The camera is not responding. Please try again later.")
                    }
                }
            })
    }

    private fun navigate(id: Int) {

        activity?.run {
            findNavController().navigate(id)
        }
    }

    private fun showAlert(message: String) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                activity?.finish()
            }
            .create()
    }

    private fun onCreateAnalyzerConfigBuilder(): ImageAnalysis {

        return ImageAnalysis.Builder()
            .setTargetResolution(targetResolution)
            .setTargetRotation(Surface.ROTATION_0)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // note: cannot set aspect ratio and target resolution on the same config
    }

    private fun onCreatePreviewConfigBuilder(): Preview {

        return Preview.Builder()
            .setTargetResolution(targetResolution)
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    private fun createCaptureUseCase(): ImageCapture {

        return  ImageCapture.Builder()
            .setTargetResolution(targetResolution)
            .setTargetRotation(Surface.ROTATION_0)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .build()
    }
}