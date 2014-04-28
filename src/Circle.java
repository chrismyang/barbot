import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvPoint3D32f;

public class Circle {
    private float x;
    private float y;
    private float radius;

    public Circle(CvPoint3D32f p) {
        this.x = p.x();
        this.y = p.y();
        this.radius = p.z();
    }

    public opencv_core.CvPoint getCenter() {
        return new opencv_core.CvPoint((int) getX(), (int) getY());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public Circle withRadius(double newRadius) {
        return new Circle(new CvPoint3D32f(getX(), getY(), newRadius));
    }
}
