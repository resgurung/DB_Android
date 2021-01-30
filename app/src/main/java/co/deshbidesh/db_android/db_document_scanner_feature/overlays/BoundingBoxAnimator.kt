package co.deshbidesh.db_android.db_document_scanner_feature.overlays

import co.deshbidesh.db_android.db_document_scanner_feature.model.EdgePoint


/** This class help to fill the gap in between the image analysis where the receive rect is zero.
 * The number is the amount of time this class uses from the last rect before removing the bounding box
 * from the screen*/

typealias EdgePointsListener = (points: List<EdgePoint>?) -> Unit

class BoundingBoxAnimator(private var number: Int,
                          private val listener: EdgePointsListener
                          ): ArObjectTracker() {

    private var counter = 0

    private var lastArObject: ArObject? = null

    override fun processObject(arObject: ArObject?) {

        var tempArObject: ArObject? = null

        arObject?.let { currArObject ->

            if (!currArObject.boundingBox.isEmpty) {

                lastArObject = currArObject

                counter = 0

                tempArObject = currArObject

            } else {

                if (!counter()) {

                   tempArObject = lastArObject

                }
            }
        }

        listener(tempArObject?.points)

        super.processObject(tempArObject?.copy())

        tempArObject = null
    }

    private fun counter(): Boolean {

        return if (counter <= number) {

            counter += 1

            false

        } else {

            lastArObject = null

            reset()

            true
        }
    }


    private fun reset() {

        counter = 0
    }

    fun setAnimationNumber(num: Int) {

        number = num
    }
    
}