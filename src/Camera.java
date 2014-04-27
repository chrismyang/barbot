import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class Camera {
    public static void main(String[] args) {
        cvNamedWindow("Camera_Output", 1);    //Create window
        CvCapture capture = cvCreateCameraCapture(CV_CAP_ANY);  //Capture using any camera connected to your system

        while(true){ //Create infinte loop for live streaming

            IplImage frame = cvQueryFrame(capture); //Create image frames from capture
            cvShowImage("Camera_Output", frame);   //Show image frames on created window
            int key = cvWaitKey(10);     //Capture Keyboard stroke
            if (key == 27){
                break;      //If you hit ESC key loop will break.
            }
        }
        cvReleaseCapture(capture); //Release capture.
        cvDestroyWindow("Camera_Output"); //Destroy Window
    }
}
