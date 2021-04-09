package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDbDocScanSelectionBinding
import co.deshbidesh.db_android.db_document_scanner_feature.viewmodel.SharedViewModel
import co.deshbidesh.db_android.shared.extensions.hasPermission
import co.deshbidesh.db_android.shared.extensions.showAlert
import co.deshbidesh.db_android.shared.utility.DBPermissionConstant
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader


class DBDocScanSelectionFragment : Fragment() {


    companion object{

        const val TAG = "DScanSelectionFragment"

        val cameraPermissions = arrayOf(DBPermissionConstant.CameraPermission)

        val externalStoragePermissions = arrayOf(DBPermissionConstant.ReadExternalStorage)
    }

    private val loader = object: BaseLoaderCallback(context) {

        override fun onManagerConnected(status: Int) {
            super.onManagerConnected(status)

            when (status) {

                LoaderCallbackInterface.SUCCESS -> {

                    sharedViewModel.libraryLoaded = true

                } else -> {

                    super.onManagerConnected(status)
                }
            }
        }
    }

    private var _binding: FragmentDbDocScanSelectionBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDbDocScanSelectionBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(
            view: View,
            savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!sharedViewModel.libraryLoaded) {

            loadOpenCVLibrary()
        }

        binding.docScanSelectionFragmentCameraButton.setOnClickListener { askCameraPermission() }

        binding.docScanSelectionFragmentGalleryButton.setOnClickListener { askExternalStorage() }

        sharedViewModel.setRoute(SharedViewModel.Route.SELECTION_FRAGMENT)
    }

    override fun onResume() {
        super.onResume()

        sharedViewModel.clearFirst()

        sharedViewModel.clearSecond()

        sharedViewModel.uri = null
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DBPermissionConstant.readExternalStoragePermissionCode
                && resultCode == Activity.RESULT_OK) {

            val uri: Uri? = data?.data

            uri?.let { currUri ->

                if (sharedViewModel.libraryLoaded) {

                    sharedViewModel.uri = currUri

                    navigateTo(SharedViewModel.Route.INTERN_FRAGMENT)

                } else {
                    showAlert("Something went wrong, please try again later")
                }

            } ?: run { Log.d(TAG, "URI empty") }

        }
        else { Log.d(TAG, "wrong request code: $requestCode") }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            DBPermissionConstant.readExternalStoragePermissionCode -> {

                if (permission(grantResults)) {

                    openMediaGallery(DBPermissionConstant.readExternalStoragePermissionCode)
                }

            }
            DBPermissionConstant.cameraPermissionCode -> {

                if (permission(grantResults)) {

                    navigateTo(SharedViewModel.Route.CAMERA_FRAGMENT)

                }
            }
        }
    }

    private fun openMediaGallery(requestCode: Int) {

        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "image/*"

        startActivityForResult(intent, requestCode)
    }

    private fun navigate(navigationId: Int) {

        activity?.runOnUiThread {

            findNavController().navigate(navigationId)
        }
    }

    private fun navigateTo(route: SharedViewModel.Route) {

        if (sharedViewModel.libraryLoaded) {

            when(route) {

                SharedViewModel.Route.CAMERA_FRAGMENT -> {

                    navigate(R.id.action_DBDocScanSelectionFragment_to_DBDocScanCameraFragment)
                }
                SharedViewModel.Route.INTERN_FRAGMENT -> {

                    navigate(R.id.action_DBDocScanSelectionFragment_to_DBDocScanInternFragment)
                }
                else -> {}
            }
        } else {

            showAlert("Something went wrong, please try again later")
        }

    }

    private fun permission(grantResults: IntArray): Boolean {

        return if(grantResults.isNotEmpty()) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                true

            } else {

                showAlert("DeshBidesh App will require permission to work")

                false
            }
        } else {

            showAlert("Something went wrong. Please try again later")

            false
        }
    }

    private fun loadOpenCVLibrary() {

        if (!OpenCVLoader.initDebug()) {

            Log.e(TAG, "Failed to load OpenCV lib")

        } else {

            loader.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

    private fun askExternalStorage() {

        if (activity?.hasPermission(*externalStoragePermissions) == true) {

            openMediaGallery(DBPermissionConstant.readExternalStoragePermissionCode)

        } else {

            requestPermissions(
                    externalStoragePermissions,
                    DBPermissionConstant.readExternalStoragePermissionCode
            )
        }
    }

    private fun askCameraPermission() {

        if (activity?.hasPermission(*cameraPermissions) == true) {

            navigateTo(SharedViewModel.Route.CAMERA_FRAGMENT)

        } else {

            requestPermissions(
                    cameraPermissions,
                    DBPermissionConstant.cameraPermissionCode
            )
        }
    }
}