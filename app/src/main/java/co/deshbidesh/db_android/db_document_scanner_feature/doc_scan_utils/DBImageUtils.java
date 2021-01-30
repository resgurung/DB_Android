package co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;

public class DBImageUtils {

    public static Bitmap rotateBitmap(Bitmap original, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
    }

    public static Mat bitmapToMat(Bitmap bitmap) {
        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8U, new Scalar(4));
        Bitmap bitmap32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bitmap32, mat);
        return mat;
    }

    public static Bitmap matToBitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Rect predictionRect(MatOfPoint2f matOfPoint2f) {

        return Imgproc.boundingRect(matOfPoint2f);
    }

    public static Mat jpegToMat(Image image) {

        ByteBuffer bb = image.getPlanes()[0].getBuffer();
        byte[] buf = new byte[bb.remaining()];
        bb.get(buf);
        Mat mat = Imgcodecs.imdecode(new MatOfByte(buf), Imgcodecs.IMREAD_UNCHANGED);
        return mat;

    }

}
