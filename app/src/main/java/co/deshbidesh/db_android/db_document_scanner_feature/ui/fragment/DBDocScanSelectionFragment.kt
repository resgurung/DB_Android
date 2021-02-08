package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.deshbidesh.db_android.R
import co.deshbidesh.db_android.databinding.FragmentDBDocScanSelectionBinding
import co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils.DBImageUtils
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanURI
import co.deshbidesh.db_android.shared.extensions.askPermission
import co.deshbidesh.db_android.shared.extensions.hasPermission
import co.deshbidesh.db_android.shared.utility.DBPermissionConstant
import com.robin.cameraxtutorial.camerax.viewmodel.SharedViewModel
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader


class DBDocScanSelectionFragment : Fragment() {

    companion object{

        const val TAG = "DScanSelectionFragment"

        val cameraPermissions = arrayOf(DBPermissionConstant.CameraPermission)

        val externalStoragePermissions = arrayOf(DBPermissionConstant.ReadExternalStorage)
    }

    private var _binding: FragmentDBDocScanSelectionBinding? = null

    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentDBDocScanSelectionBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.docScanSelectionFragmentCameraButton.setOnClickListener {

            if (activity?.hasPermission(*cameraPermissions) == true) {

                navigate(R.id.action_scan_selectionFragment_to_cameraFragment)

            } else {

                activity?.askPermission(*cameraPermissions, requestCode = DBPermissionConstant.cameraPermissionCode)

            }
        }

        binding.docScanSelectionFragmentGalleryButton.setOnClickListener {

            if (activity?.hasPermission(*externalStoragePermissions) == true) {

                openMediaGallery(DBPermissionConstant.readExternalStoragePermissionCode)

            } else {

                activity?.askPermission(*externalStoragePermissions, requestCode = DBPermissionConstant.readExternalStoragePermissionCode)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DBPermissionConstant.readExternalStoragePermissionCode && resultCode == Activity.RESULT_OK) {

            val uri: Uri? = data?.data

            uri?.let { currUri ->

                val uriHolder = DBDocScanURI(currUri)

                val direction = DBDocScanSelectionFragmentDirections.actionScanSelectionFragmentToInternFragment(uriHolder)

                findNavController().navigate(direction)

            } ?: run {

                println("debug: uri null")
            }
        }else {

            println("debug: wrong request code: $requestCode")
        }
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

                    navigate(R.id.action_scan_selectionFragment_to_cameraFragment)
                }
            }
        }

    }

    private fun openMediaGallery(requestCode: Int) {

        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "image/*"

        startActivityForResult(intent, requestCode)
    }

    private fun navigate(id: Int) {

        activity?.runOnUiThread {

            findNavController().navigate(id)
        }
    }

    private fun permission(grantResults: IntArray): Boolean {

        return if(grantResults.isNotEmpty()) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                showAlert("Desh Bidesh App will require permission to work")
                false
            }
        } else {

            showAlert("Something went wrong. Please try again later")
            false
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

}