package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.BuildConfig
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbDocScanCameraBinding
import co.deshbidesh.db_android.db_document_scanner_feature.factories.DocumentAnalyzerViewModelFactory
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBScannedObjectInfo
import co.deshbidesh.db_android.db_document_scanner_feature.overlays.*
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.DocumentAnalyzer
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.SharedViewModel
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.ThreadedImageAnalyzer
import co.deshbidesh.db_android.shared.utility.DBCircularProgressBar
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class DBDocScanCameraFragment : Fragment() {

    companion object{

        const val TAG = "DBDocScanCameraFragment"

        const val NUMBER_OF_TIMES_LAST_POINTS_TO_BE_USED = 4

        val targetResolution: Size = Size(720, 1280)

    }

    private var _binding: FragmentDbDocScanCameraBinding? = null

    private val binding get() = _binding!!

    private val mutableArOverlayView = MutableLiveData<ArOverlayView>()

    private val arOverlayView: LiveData<ArOverlayView> = mutableArOverlayView

    var cameraRunning = false
         set(value) {

             field = value

             hideDialog()
         }

    private var imageAnalyzer: ThreadedImageAnalyzer? = null

    private var scannedObjectInfo: DBScannedObjectInfo? = null

    private lateinit var viewModelFactory: DocumentAnalyzerViewModelFactory

    private lateinit var imageAnalyzerViewModel: DocumentAnalyzer

    private var imageCapture: ImageCapture? = null

    private lateinit var animator: BoundingBoxAnimator

    // Select back camera as a default. We will only use back camera
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private val cameraProviderFuture by lazy { context?.let { ProcessCameraProvider.getInstance(it) } }

    private val executor: Executor by lazy { Executors.newSingleThreadExecutor() }

    private val loadingDialog: DBCircularProgressBar by lazy { DBCircularProgressBar(requireActivity()) }

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDbDocScanCameraBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog.loadAlertDialog()

        mutableArOverlayView.postValue(binding.arOverlays) // background thread

        animator = BoundingBoxAnimator(NUMBER_OF_TIMES_LAST_POINTS_TO_BE_USED) { info ->

            scannedObjectInfo = info
        }

        viewModelFactory = DocumentAnalyzerViewModelFactory(sharedViewModel.opencvHelper)

        imageAnalyzerViewModel = ViewModelProvider(this, viewModelFactory).get(DocumentAnalyzer::class.java)

        imageAnalyzer = imageAnalyzerViewModel

        val boundingBoxArOverlay = BoundingBoxArOverlay(requireContext(), BuildConfig.DEBUG)

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

        binding.cameraCaptureButton.isEnabled = false

        binding.cameraCaptureButton.setOnClickListener {

            binding.cameraCaptureButton.isEnabled = false

            scannedObjectInfo?.let {

                processImage(it)

            } ?: run {

                saveImageToMemory()
            }
        }

        updateRoute(view)
    }

    private fun updateRoute(view: View) {

        when(sharedViewModel.getRoute()) {
            SharedViewModel.Route.RESULT_FRAGMENT -> {

                sharedViewModel.clearFirst()

                sharedViewModel.clearSecond()

            }
            SharedViewModel.Route.INTERN_FRAGMENT -> {

                sharedViewModel.clearFirst()

                sharedViewModel.clearSecond()
            }
            else -> {}
        }

        sharedViewModel.setRoute(SharedViewModel.Route.CAMERA_FRAGMENT)
    }

    override fun onResume() {
        super.onResume()

        startCamera()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (cameraRunning) {

            cameraProviderFuture?.get()?.unbindAll()

            imageCapture = null

            cameraRunning = false

            Log.i("CameraFragment", "Stopping camera")
        }

        _binding = null
    }

    private fun hideDialog() {

        loadingDialog.hideAlertDialog()

        binding.cameraCaptureButton.isEnabled = true

    }

    @SuppressLint("UnsafeExperimentalUsageError")
    private fun startCamera() = binding.preview.post {

        cameraProviderFuture?.addListener(Runnable {

            try {

                // Make sure that there are no other use cases bound to CameraX
                activity?.runOnUiThread {
                    cameraProviderFuture?.get()?.unbindAll()
                }

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

    private fun processImage(objectInfo: DBScannedObjectInfo) {

        sharedViewModel.processImageWithObjectInfo(objectInfo) { processComplete ->

            if (processComplete) {

                navigateTo(SharedViewModel.Route.RESULT_FRAGMENT)

            } else {

                saveImageToMemory()
            }
        }
    }
    private fun saveImageToMemory() {

        imageCapture?.takePicture(executor,
            object: ImageCapture.OnImageCapturedCallback() {

                override fun onError(exception: ImageCaptureException) {

                    activity?.runOnUiThread {

                        showAlert("Photo capture failed: ${exception.message}")
                    }

                }

                @SuppressLint("UnsafeExperimentalUsageError")
                override fun onCaptureSuccess(image: ImageProxy) {

                    image.image?.let { currentImage ->

                        sharedViewModel.processImage(
                                currentImage) { route ->
                            navigateTo(route)
                        }

                    } ?: run {

                        showAlert("The camera is not responding. Please try again later.")
                    }

                    super.onCaptureSuccess(image)
                }
            })
    }

    private fun navigate(id: Int) {

        activity?.run {

            findNavController().navigate(id)
        }
    }

    fun navigateTo(fragment: SharedViewModel.Route) {

        when(fragment) {
            SharedViewModel.Route.INTERN_FRAGMENT -> {

                navigate(R.id.action_DBDocScanCameraFragment_to_DBDocScanInternFragment)

            }
            SharedViewModel.Route.RESULT_FRAGMENT -> {

                navigate(R.id.action_DBDocScanCameraFragment_to_DBDocScanResultProcessFragment)

            }
            else -> {}
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
                //.setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetResolution(targetResolution)
                .setTargetRotation(Surface.ROTATION_0)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

        //.setTargetResolution(targetResolution)
        // note: cannot set aspect ratio and target resolution on the same config
    }

    private fun onCreatePreviewConfigBuilder(): Preview {

        return Preview.Builder()
                //.setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetResolution(targetResolution)
                .setTargetRotation(Surface.ROTATION_0)
                .build()

        //.setTargetResolution(targetResolution)
    }

    private fun createCaptureUseCase(): ImageCapture {

        return  ImageCapture.Builder()
                //.setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetResolution(targetResolution)
                //.setTargetRotation(Surface.ROTATION_0)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

        //.setTargetResolution(targetResolution)
    }
}

//    private fun saveImageToFile() {
//
//        Log.d(TAG, "saved saving ..")
//        val dir  = fileUtils.createDirectoryIfNotExist()
//        val file = fileUtils.makeFile(dir, "tempFile")
//        val fileBuilder = ImageCapture.OutputFileOptions.Builder(file).build()
//
//        imageCapture?.takePicture(fileBuilder, executor, object: ImageCapture.OnImageCapturedCallback(), ImageCapture.OnImageSavedCallback {
//            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//
//                outputFileResults.savedUri?.let {
//
//                    Log.d(TAG, "saved uri: $it")
//
//                    val uriHolder = DBDocScanURI(it)
//
//                    val direction = DBDocScanCameraFragmentDirections.actionScanCameraFragmentToInternFragment(uriHolder)
//
//                    findNavController().navigate(direction)
//
//                } ?: run {
//
//                    showAlert("Image cannot be saved")
//                }
//
//            }
//
//            override fun onError(exception: ImageCaptureException) {
//
//                super.onError(exception)
//
//                activity?.runOnUiThread {
//
//                    showAlert("Photo capture failed: ${exception.message}")
//                }
//            }
//
//        })
//    }