package co.deshbidesh.db_android.shared.extensions

import android.graphics.*

// extension function to change bitmap brightness and contrast
fun Bitmap.setBrightnessContrast(
        brightness: Float = 0.0F,
        contrast: Float = 1.0F
): Bitmap?{
    val bitmap = copy(
            Bitmap.Config.ARGB_8888,
            true)
    val paint = Paint()

    // brightness -200..200, 0 is default
    // contrast 0..2, 1 is default
    // you may tweak the range
    val matrix = ColorMatrix(
            floatArrayOf(
                    contrast, 0f, 0f, 0f, brightness,
                    0f, contrast, 0f, 0f, brightness,
                    0f, 0f, contrast, 0f, brightness,
                    0f, 0f, 0f, 1f, 0f
            )
    )

    val filter = ColorMatrixColorFilter(matrix)

    paint.colorFilter = filter

    Canvas(bitmap).drawBitmap(
            this,
            0f,
            0f,
            paint
    )
    return bitmap
}

// extension function to change bitmap saturation
fun Bitmap.setSaturation(
        saturation: Float
): Bitmap? {

    val bitmap = copy(
            Bitmap.Config.ARGB_8888,
            true
    )

    val paint = Paint()

    val colorMatrix = ColorMatrix()

    colorMatrix.setSaturation(saturation)

    val filter = ColorMatrixColorFilter(colorMatrix)

    paint.colorFilter = filter

    Canvas(bitmap).drawBitmap(
            this,
            0f,
            0f,
            paint
    )

    return bitmap
}