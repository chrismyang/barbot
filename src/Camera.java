import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class Camera {
    public static void main(String[] args) {
        cvNamedWindow("Camera_Output", 1);    //Create window
        CvCapture capture = cvCreateCameraCapture(CV_CAP_ANY);  //Capture using any camera connected to your system

        while(true){ //Create infinte loop for live streaming

            IplImage frame = cvQueryFrame(capture); //Create image frames from capture

            List<CvScalar> range = loadRange();

//            cvSaveImage("ping-pong.jpg", frame);

            IplImage out = new Smoother().smooth(
                    frame,
                    range.get(0),
                    range.get(1)
            );

            cvShowImage("Camera_Output", out);   //Show image frames on created window

//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            int key = cvWaitKey(10);     //Capture Keyboard stroke
            if (key == 27){
                break;      //If you hit ESC key loop will break.
            }
        }
        cvReleaseCapture(capture); //Release capture.
        cvDestroyWindow("Camera_Output"); //Destroy Window
    }

    public static List<CvScalar> loadRange() {
        List<String> lines = readFile("range.txt");
        List<CvScalar> out = new ArrayList<>();
        out.add(parseTriple(lines.get(0)));
        out.add(parseTriple(lines.get(1)));
        return out;
    }

    public static CvScalar parseTriple(String x) {
        String[] values = x.split(",");
        return new CvScalar(
                Double.parseDouble(values[0]),
                Double.parseDouble(values[1]),
                Double.parseDouble(values[2]),
                Double.parseDouble(values[3]));
    }

    private static List<String> readFile( String file ) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new FileReader(file));
            String         line = null;
            String         ls = System.getProperty("line.separator");

            List<String> lines = new ArrayList<>();

            while( ( line = reader.readLine() ) != null ) {
                lines.add(line);
            }

            return lines;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
