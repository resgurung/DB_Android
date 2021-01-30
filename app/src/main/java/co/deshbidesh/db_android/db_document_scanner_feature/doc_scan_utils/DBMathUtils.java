package co.deshbidesh.db_android.db_document_scanner_feature.doc_scan_utils;

import org.opencv.core.CvType;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

public class DBMathUtils {

    public static MatOfPoint toMatOfPointInt(MatOfPoint2f mat) {
        MatOfPoint matInt = new MatOfPoint();
        mat.convertTo(matInt, CvType.CV_32S);
        return matInt;
    }

    public static MatOfPoint2f toMatOfPointFloat(MatOfPoint mat) {
        MatOfPoint2f matFloat = new MatOfPoint2f();
        mat.convertTo(matFloat, CvType.CV_32FC2);
        return matFloat;
    }

    public static double angle(Point p1, Point p2, Point p0) {
        double dx1 = p1.x - p0.x;
        double dy1 = p1.y - p0.y;
        double dx2 = p2.x - p0.x;
        double dy2 = p2.y - p0.y;
        return (dx1 * dx2 + dy1 * dy2) / Math.sqrt((dx1 * dx1 + dy1 * dy1) * (dx2 * dx2 + dy2 * dy2) + 1e-10);
    }

    public static double calculateAngle(Point p1, Point p2, Point p3
                                        //double P1X, double P1Y, double P2X, double P2Y, double P3X, double P3Y) {
        ){

        double numerator = p2.y * (p1.x - p3.x) + p1.y * (p3.x - p2.x) + p3.y * (p2.x - p1.x);
        //double numerator = P2Y*(P1X-P3X) + P1Y*(P3X-P2X) + P3Y*(P2X-P1X);
        //double denominator = (P2X-P1X)*(P1X-P3X) + (P2Y-P1Y)*(P1Y-P3Y);
        double denominator = (p2.x - p1.x) * (p1.x - p3.x) + (p2.y - p1.y) * (p1.y - p3.y);
        double ratio = numerator/denominator;

        double angleRad = Math.atan(ratio);
        double angleDeg = (angleRad * 180) / Math.PI;

        if(angleDeg < 0){
            angleDeg = 180 + angleDeg;
        }

        return angleDeg;
    }

    public static MatOfPoint2f scaleRectangle(MatOfPoint2f original, double scale) {
        List<Point> originalPoints = original.toList();
        List<Point> resultPoints = new ArrayList<Point>();

        for (Point point : originalPoints) {
            resultPoints.add(new Point(point.x * scale, point.y * scale));
        }

        MatOfPoint2f result = new MatOfPoint2f();
        result.fromList(resultPoints);
        return result;
    }

    public static List<Point> matOfPoint2fToPoint(MatOfPoint2f matOfPoint2f) {

        List<Point> points = new ArrayList<>();

        if (matOfPoint2f != null) {

            for (Point point : matOfPoint2f.toArray()) {

                points.add(new Point(point.x, point.y));
            }
        }
        return points;
    }

}
