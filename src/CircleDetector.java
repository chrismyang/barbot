import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HOUGH_GRADIENT;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvHoughCircles;

/**
 * Class that wraps the fairly complicated invocation sequence of using OpenCV's Hough circles.
 */
public class CircleDetector {
    public List<Circle> findCircles(IplImage input, double inverseRatio, double minDistance, double cannyEdgeThreshold, double thresholdCenterDetection, int minRadius, int maxRadius) {
        CvMemStorage mem = CvMemStorage.create();

        try {
            IplImage gray = cvCreateImage(cvGetSize(input), 8, 1);
            if (input.nChannels() == 3) {
                cvCvtColor(input, gray, CV_BGR2GRAY);
            } else {
                cvCopy(input, gray);
            }

            CvSeq circles = cvHoughCircles(
                    gray, //Input image
                    mem, //Memory Storage
                    CV_HOUGH_GRADIENT, //Detection method
                    inverseRatio, //Inverse ratio
                    minDistance, //Minimum distance between the centers of the detected circles
                    cannyEdgeThreshold, //Higher threshold for canny edge detector
                    thresholdCenterDetection, //Threshold at the center detection stage
                    minRadius, //min radius
                    maxRadius //max radius
            );

            List<Circle> c = new ArrayList<>();

            for(int i = 0; i < circles.total(); i++){
                c.add(new Circle(new CvPoint3D32f(cvGetSeqElem(circles, i))));
            }

            return c;
        } finally {
            cvReleaseMemStorage(mem);
        }
    }
}
