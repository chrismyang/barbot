import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

public class Tester {
    public static void main(String[] args) {
        Image img = new Image("Tennis-morph.png");

        cvSmooth(img.getImage(), img.getImage(), CV_GAUSSIAN, 15, 15, 0, 0);

        List<Circle> circles = new CircleDetector().findCircles(img.getImage(), 4, img.getSize().height() / 10, 100, 40, 0, 0);

        System.out.println(circles.size());



        IplImage out = IplImage.create(img.getSize(), img.getImage().depth(), img.getImage().nChannels());
        cvCopy(img.getImage(), out);

        for (Circle circle : circles) {
            CvPoint center = circle.getCenter();

            CvScalar val = img.getValue(center);

            if (val.val(0) >= 1) {
                cvCircle(out,  center, 3,  CV_RGB(0,255,0), -1, CV_AA, 0);
                cvCircle(out,  center, Math.round(circle.getRadius()), CV_RGB(255,0,0),  3, CV_AA, 0);
            } else {
//                cvCircle(out,  center, 3,  CV_RGB(0,255,0), -1, CV_AA, 0);
//                cvCircle(out,  center, Math.round(circle.getRadius()), CV_RGB(0,0,255),  3, CV_AA, 0);
            }
        }

        cvSaveImage("smooth-circles.jpg", out);
    }
}
