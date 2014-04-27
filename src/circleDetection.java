import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class circleDetection{
    public static void main(String[] args){
        IplImage src = cvLoadImage("img2.png");

//        IplImage gray = cvCreateImage(cvGetSize(src), 8, 1);

//        cvCvtColor(src, gray, CV_BGR2GRAY);
        cvSmooth(src, src, CV_GAUSSIAN, 3);

        List<Circle> circles = new CircleDetector().findCircles(
                src,
                1, //Inverse ratio
                100, //Minimum distance between the centers of the detected circles
                100, //Higher threshold for canny edge detector
                100, //Threshold at the center detection stage
                15, //min radius
                500 //max radius
        );

        for (Circle circle : circles) {
            cvCircle(src, circle.getCenter(), Math.round(circle.getRadius()), CvScalar.GREEN, 6, CV_AA, 0);
        }

        cvSaveImage("img2-output.jpg", src);

    }
}