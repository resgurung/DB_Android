package co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.exifinterface.media.ExifInterface;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import co.deshbidesh.db_android.db_document_scanner_feature.model.DBDocScanImage;

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

    public static String getAbsolutePathFor(Context context, Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = context.getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            /**
             * ExifInterface exif = new ExifInterface(src);
             * orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            Class<?> exifClass = Class.forName("android.media.ExifInterface");
            Constructor<?> exifConstructor = exifClass.getConstructor(String.class);
            Object exifInstance = exifConstructor.newInstance(src);
            Method getAttributeInt = exifClass.getMethod("getAttributeInt", String.class, int.class);
            Field tagOrientationField = exifClass.getField("TAG_ORIENTATION");
            String tagOrientation = (String) tagOrientationField.get(null);
            orientation = (Integer) getAttributeInt.invoke(exifInstance, new Object[] { tagOrientation, 1});
        } catch (ClassNotFoundException
                | SecurityException
                | NoSuchMethodException
                | IllegalArgumentException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return orientation;
    }

    public static DBDocScanImage rotateBitmapWith(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            if (orientation == 1) {
                return new DBDocScanImage(bitmap, orientation);
            }

//            Matrix matrix = new Matrix();
//            switch (orientation) {
//                case 2:
//                    matrix.setScale(-1, 1);
//                    break;
//                case 3:
//                    matrix.setRotate(180);
//                    break;
//                case 4:
//                    matrix.setRotate(180);
//                    matrix.postScale(-1, 1);
//                    break;
//                case 5:
//                    matrix.setRotate(90);
//                    matrix.postScale(-1, 1);
//                    break;
//                case 6:
//                    matrix.setRotate(90);
//                    break;
//                case 7:
//                    matrix.setRotate(-90);
//                    matrix.postScale(-1, 1);
//                    break;
//                case 8:
//                    matrix.setRotate(-90);
//                    break;
//                default:
//                    return new DBDocScanImage(bitmap, orientation);
//            }
//
//            try {
//                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                bitmap.recycle();
//                return new DBDocScanImage(oriented, orientation);
//            } catch (OutOfMemoryError e) {
//                e.printStackTrace();
//                return new DBDocScanImage(bitmap, orientation);
//            }

            DBDocScanImage imgObj = rotateByOrientation(bitmap, orientation);

            if (imgObj != null) {

                return imgObj;

            } else {

                return new DBDocScanImage(bitmap, 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new DBDocScanImage(bitmap, 0);
    }

//    public static Bitmap rotateByOrientation(int orientation, Bitmap bitmap) {
//
//        Matrix matrix = new Matrix();
//        switch (orientation) {
//            case 2:
//                matrix.setScale(-1, 1);
//                break;
//            case 3:
//                matrix.setRotate(180);
//                break;
//            case 4:
//                matrix.setRotate(180);
//                matrix.postScale(-1, 1);
//                break;
//            case 5:
//                matrix.setRotate(90);
//                matrix.postScale(-1, 1);
//                break;
//            case 6:
//                matrix.setRotate(90);
//                break;
//            case 7:
//                matrix.setRotate(-90);
//                matrix.postScale(-1, 1);
//                break;
//            case 8:
//                matrix.setRotate(-90);
//                break;
//            default:
//                return bitmap;
//        }
//
//        try {
//            Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            bitmap.recycle();
//            return oriented;
//        } catch (OutOfMemoryError e) {
//            e.printStackTrace();
//            return bitmap;
//        }
//    }

    public static DBDocScanImage rotateByOrientation(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();

        switch (orientation) {
//            case ExifInterface.ORIENTATION_NORMAL:
//                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return null;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return new DBDocScanImage(bmRotated, orientation);
            //return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

}
