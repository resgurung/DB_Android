package co.deshbidesh.db_android.db_document_scanner_feature.model

import android.media.Image
import org.opencv.core.Mat


class DBScannedObjectInfo(
    var points: List<EdgePoint>,
    var rotation: Int,
    var imageMat: Mat
    )