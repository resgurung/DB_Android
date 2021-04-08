package co.deshbidesh.db_android.db_document_scanner_feature.overlays

import android.graphics.RectF
import android.util.Log
import android.util.Size
import kotlin.math.ceil


/**
 * Allows the tracked ArObject to be mapped to a other coordinate system, e.g. a view.
 *
 * We use this to map the coordinate system of the preview image we received from the camera API to the ArOverlayView's
 * coordinate system which will have an other size.
 */
class PositionTranslator(
    private val targetWidth: Int,
    private val targetHeight: Int,
    private val frontFacing: Boolean = false
) : ArObjectTracker() {

    companion object {

        const val TAG = "ArObjectTracker"
    }

    override fun processObject(arObject: ArObject?) {
        if (arObject != null) {

            // Rotate Size
            val rotatedSize = when (arObject.sourceRotationDegrees) {
                90, 270 -> Size(arObject.sourceSize.height, arObject.sourceSize.width)
                0, 180 -> arObject.sourceSize
                else -> throw IllegalArgumentException("Unsupported rotation. Must be 0, 90, 180 or 270")
            }


            // Calculate scale
            val scaleX = targetWidth / rotatedSize.width.toDouble()
            val scaleY = targetHeight / rotatedSize.height.toDouble()
            val scale = scaleX.coerceAtLeast(scaleY)
            val scaleF = scale.toFloat()
            val scaledSize = Size(ceil(rotatedSize.width * scale).toInt(), ceil(rotatedSize.height * scale).toInt())

            // Calculate offset (we need to center the overlay on the target)
            val offsetX = (targetWidth - scaledSize.width) / 2
            val offsetY = (targetHeight - scaledSize.height) / 2

            // Map bounding box
            val mappedBoundingBox = RectF().apply {
                left = arObject.boundingBox.left * scaleF + offsetX
                top = arObject.boundingBox.top * scaleF + offsetY
                right = arObject.boundingBox.right * scaleF + offsetX
                bottom = arObject.boundingBox.bottom * scaleF + offsetY
            }

            // The front facing image is flipped, so we need to mirrow the positions on the vertical axis (centerX)
            if (frontFacing) {
                val centerX = targetWidth / 2
                mappedBoundingBox.left = centerX + (centerX - mappedBoundingBox.left)
                mappedBoundingBox.right = centerX - (mappedBoundingBox.right - centerX)
            }

            super.processObject(
                arObject.copy(
                    boundingBox = mappedBoundingBox,
                    sourceSize = Size(targetWidth, targetHeight)
                )
            )
        } else {
            super.processObject(null)
        }
    }

    private fun RectF.toSize() = Size(width().toInt(), height().toInt())
}