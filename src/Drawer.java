import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class Drawer {
    private IplImage baseImage;

    public Drawer(IplImage baseImage) {
        this.baseImage = baseImage;
    }

    public IplImage drawCircles(List<Circle> circles, CvScalar color, int thickness, int line_type) {
        IplImage out = copyBaseImage();

        for (Circle circle : circles) {
            cvCircle(out,  circle.getCenter(), Math.round(circle.getRadius()), color, thickness, line_type, /* no idea what "shift" does */ 0);
        }

        return out;
    }

    private IplImage copyBaseImage() {
        CvSize size = cvGetSize(baseImage);
        IplImage out = IplImage.create(size, baseImage.depth(), baseImage.nChannels());
        cvCopy(baseImage, out);
        return out;
    }
}
