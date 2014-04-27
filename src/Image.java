import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;

public class Image {
    private IplImage image;

    public Image(String filename) {
        IplImage image = cvLoadImage(filename);

        if (image == null) {
            throw new IllegalArgumentException("Could not find image file " + filename);
        }

        this.image = image;
    }

    private Image(IplImage image) {
        this.image = image;
    }

    public IplImage getImage() {
        return image;
    }

    public CvSize getSize() {
        return cvGetSize(image);
    }

    public CvScalar getValue(CvPoint point) {
        return cvGet2D(getImage(), point.y(), point.x());
    }

//    public Image copy() {
////        IplImage copy = cvCreateImage(getSize(), cvGet, cv);
//
//
////        return new Image(copy);
//
//    }
}
