import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.CanvasFrame;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class Smoother {
    public IplImage smooth(IplImage img, CvScalar lower, CvScalar upper) {
        CvSize size = cvGetSize(img);
        IplImage hsv = cvCreateImage(size, IPL_DEPTH_8U, 3);
        cvCvtColor(img, hsv, CV_BGR2HSV);

        cvSaveImage("hsv.jpg", hsv);

        CvMat mask = cvCreateMat(size.height(), size.width(), CV_8UC1);
        cvInRangeS(hsv, lower, upper, mask);
        cvReleaseImage(hsv);

        cvSaveImage("mask.jpg", mask);

        IplConvKernel se21 = cvCreateStructuringElementEx(21, 21, 10, 10, CV_SHAPE_RECT, null);
        IplConvKernel se11 = cvCreateStructuringElementEx(11, 11, 5,  5,  CV_SHAPE_RECT, null);

        cvMorphologyEx(mask, mask, null, se21, MORPH_CLOSE, 1);
        cvMorphologyEx(mask, mask, null, se11, MORPH_OPEN, 1);

//        cvClose(mask, mask, se21); // See completed example for cvClose definition
//        cvOpen(mask, mask, se11);  // See completed example for cvOpen  definition
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

    public void save(String filename) {
        IplImage img=cvLoadImage(filename);

        if (img == null) {
            throw new RuntimeException("Could not find file " + filename);
        }

        IplImage out = smooth(img, cvScalar(0.11*256, 0.60*256, 0.20*256, 0), cvScalar(0.14*256, 1.00*256, 1.00*256, 0));
        cvSaveImage("final.jpg", out);
    }

    public static void main(String[] args) throws InterruptedException {
        String fileName = "Tennis-input.jpg";
        Smoother smt = new Smoother();
        smt.save(fileName);
    }
}