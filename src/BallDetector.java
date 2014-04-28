import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class BallDetector {
    /**
     * Detects balls (circles) with a color in the given range in a single image.
     *
     * @param img image to detect balls in
     * @param lower lower bound of color range (still not super clear if this is RGB or HSV)
     * @param upper upper bound of color range (still not super clear if this is RGB or HSV)
     * @return copy of input image, but with detected balls drawn.  Probably should return a richer object (e.g., the
     * circles' locations and sizes, the mask image, etc.) will add that eventually.
     */
    public IplImage detect(IplImage img, CvScalar lower, CvScalar upper) {
        CvSize size = cvGetSize(img);

        // concert to HSV
        IplImage hsv = cvCreateImage(size, IPL_DEPTH_8U, 3);
        cvCvtColor(img, hsv, CV_BGR2HSV);

        cvSaveImage("hsv.jpg", hsv);

        // create simple mask

        CvMat mask = cvCreateMat(size.height(), size.width(), CV_8UC1);
        cvInRangeS(hsv, lower, upper, mask);
        cvReleaseImage(hsv);

        cvSaveImage("mask.jpg", mask);

        // close/open the mask to "smooth" out some noise

        IplConvKernel se21 = cvCreateStructuringElementEx(21, 21, 10, 10, CV_SHAPE_RECT, null);
        IplConvKernel se11 = cvCreateStructuringElementEx(11, 11, 5,  5,  CV_SHAPE_RECT, null);

        cvMorphologyEx(mask, mask, null, se21, MORPH_CLOSE, 1);
        cvMorphologyEx(mask, mask, null, se11, MORPH_OPEN, 1);

        cvReleaseStructuringElement(se21);
        cvReleaseStructuringElement(se11);

        cvSaveImage("morph.jpg", mask);

        /* Copy mask into a grayscale image */

        IplImage hough_in = cvCreateImage(size, 8, 1);
        cvCopy(mask, hough_in, null);
        cvSmooth(hough_in, hough_in, CV_GAUSSIAN, 15, 15, 0, 0);

        cvSaveImage("smooth.jpg", hough_in);

	    /* Run the Hough function */
        List<Circle> circles = new CircleDetector().findCircles(hough_in, 4, size.height()/10, 100, 40, 0, 0);

        List<Circle> filtered = new ArrayList<>();

        for (Circle circle : circles) {
            CvPoint center = circle.getCenter();

            CvScalar val = new Image(hough_in).getValue(center);

            if (val.val(0) >= 1) {
                filtered.add(circle);
            }
        }

        IplImage out = new Drawer(img).drawCircles(filtered, CV_RGB(0,255,0),  3, CV_AA);

        return out;
    }

    public static void main(String[] args) throws InterruptedException {
        String fileName = "Tennis-input.jpg";

        IplImage img = loadImage(fileName);

        BallDetector detector = new BallDetector();

        IplImage out = detector.detect(img, cvScalar(0.11 * 256, 0.60 * 256, 0.20 * 256, 0), cvScalar(0.14 * 256, 1.00 * 256, 1.00 * 256, 0));

        cvSaveImage("final.jpg", out);
    }

    private static IplImage loadImage(String filename) {
        IplImage img=cvLoadImage(filename);

        if (img == null) {
            throw new RuntimeException("Could not find file " + filename);
        }

        return img;
    }
}