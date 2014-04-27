import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.CanvasFrame;

import java.awt.event.KeyEvent;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class Smoother {
    public void smooth(String filename) throws InterruptedException {
        IplImage img=cvLoadImage(filename);

        if (img == null) {
            throw new RuntimeException("Could not find file " + filename);
        }

//        CanvasFrame canvas = new CanvasFrame("Out");
//        canvas.showImage(img);
//        KeyEvent key = canvas.waitKey();
//        if (key != null) {
//            return;
//        }

//        cvShowImage("foo", img);
//        cvWaitKey();

        CvSize size = cvGetSize(img);
        IplImage hsv = cvCreateImage(size, IPL_DEPTH_8U, 3);
        cvCvtColor(img, hsv, CV_BGR2HSV);

        cvSaveImage("hsv.jpg", hsv);

        CvMat mask = cvCreateMat(size.height(), size.width(), CV_8UC1);
        cvInRangeS(hsv, cvScalar(0.11*256, 0.60*256, 0.20*256, 0),
                cvScalar(0.14*256, 1.00*256, 1.00*256, 0), mask);
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

//        CvMemStorage storage = cvCreateMemStorage(0);
//        CvSeq circles = cvHoughCircles(hough_in, storage,
//                CV_HOUGH_GRADIENT, 4, size.height()/10, 100, 40, 0, 0);
//        cvReleaseMemStorage(storage);


//        int total = circles.total();
//
//        if (total == 0) {
//            throw new RuntimeException("0 total in circles");
//        }
//
//        Pointer p0 = cvGetSeqElem(circles, 0);
//        Pointer p1 = cvGetSeqElem(circles, 1);
//        Pointer p2 = cvGetSeqElem(circles, 2);

//        for (int i = 0; i < circles.total(); i++) {
//            CvPoint3D32f x = new CvPoint3D32f(cvGetSeqElem(circles, i));
//            CvPoint center = new CvPoint((byte) 0, new Double(x.x()).doubleValue(), new Double(x.y()).doubleValue());
//            CvScalar val = cvGet2D(mask, center.y(), center.x());
//            if (val.val(0) < 1) continue;
//            cvCircle(img,  center, 3,  CV_RGB(0,255,0), -1, CV_AA, 0);
//            cvCircle(img,  center, (int) x.z(), CV_RGB(255,0,0),  3, CV_AA, 0);
//            cvCircle(mask, center, 3,  CV_RGB(0,255,0), -1, CV_AA, 0);
//            cvCircle(mask, center, (int) x.z(), CV_RGB(255,0,0),  3, CV_AA, 0);
//        }


        cvSaveImage("final.jpg", img);
    }

    public static void main(String[] args) throws InterruptedException {
        String fileName = "Tennis-input.jpg";
        Smoother smt = new Smoother();
        smt.smooth(fileName);
    }
}