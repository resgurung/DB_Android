package co.deshbidesh.db_android.db_document_scanner_feature.model

import org.opencv.core.Point

data class EdgePoint(
        val x: Double,
        val y: Double
        ) {

    fun toOpenCVPoint() = Point(x,y)
}