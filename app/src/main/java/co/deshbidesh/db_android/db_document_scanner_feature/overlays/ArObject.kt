package co.deshbidesh.db_android.db_document_scanner_feature.overlays

import android.graphics.RectF
import android.util.Size
import co.deshbidesh.db_android.db_document_scanner_feature.model.DBScannedObjectInfo
import co.deshbidesh.db_android.db_document_scanner_feature.model.EdgePoint

data class ArObject(val trackingId: Int,
                    val boundingBox: RectF,
                    val sourceSize: Size,
                    val objectInfo: DBScannedObjectInfo
                    //val sourceRotationDegrees: Int,
                    //val points: List<EdgePoint>
                    )