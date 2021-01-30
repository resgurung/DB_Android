package co.deshbidesh.db_android.db_document_scanner_feature.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.deshbidesh.db_android.R


class DBDocScanCameraFragment : Fragment() {

    companion object{

        const val TAG = "DBDocScanCameraFragment"
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_d_b_doc_scan_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!OpenCVLoader.initDebug()) {

            Log.e("CameraFragment", "Failed to load OpenCV lib")

        } else {

        }
    }

}