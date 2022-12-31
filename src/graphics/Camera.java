package graphics;

import Jama.Matrix;
import javafx.geometry.Point3D;
import objects.*;

/**
 * This class implements a Camera object, used by the other graphics classes as the view point.
 */
public class Camera {
    // fields
    /**
     * 4x4 Matrix that transforms points from the World Coordinates to coordinates
     * relative to a camera.
     */
    private Matrix camToWorld;

    private Point3D camPos;

    /**
     * The forward, right and up VECTORS represent the orientation of the camera.
     * These vectors are unit vectors and are given relative the position of the camera, in world coordinates.
     */
    public Point3D forward;

    public Point3D right;

    public Point3D up;

    public double nearClipPlane = 1;
    public double farClipPlane = 250;

    // After projecting points, check if they lie in the canvas_w and canvas_h
    public final int CANVAS_W = 5;
    public final int CANVAS_H = 5;
    /**
     * Default constructor, by default the camera is positioned at the origin, facing the
     * negative z-axis.
     */
    public Camera() {
        // default cam matrix TODO: the vector directions are a bit confusing
        double[][] defCam =
                {
                        {1,0,0,0},    // x axis unit vector is {1,0,0}
                        {0,1,0,0},    // y axis unit vector is {0,1,0}
                        {0,0,-1,0},   // z axis unit vector is {0,0,1}  (BY DEFAULT POINT IN -Z)
                        {0,0,0,1}     // position of origin is {0,0,0}
                };

        right = new Point3D(1,0,0);
        up = new Point3D(0,1,0);
        forward = new Point3D(0,0,-1);

        camToWorld = new Matrix(defCam);
        // by default camPos is:
        camPos = new Point3D(0,0,0);
    }

    /**
     * Sets a new cam-to-world matrix using a 2D array.
     * @param newCam
     */
    public void setCamToWorld(double [][] newCam) {
        camToWorld = new Matrix(newCam);
    }

    public Matrix getCamToWorld() {
        return camToWorld;
    }

    public Matrix getWorldToCam() {
        return camToWorld.inverse();
    }

    /**
     * Updates the position of the camera to a point in 3D WORLD COORDINATES
     * @param point - The point, in 3D world coordinates to which our camera will move to.
     */
    public void setPos(Point3D point) {
        camToWorld.set(3,0,point.getX());
        camToWorld.set(3,1,point.getY());
        camToWorld.set(3,2,point.getZ());
        camPos = point;
    }

    /**
     * Sets the orientation axes of the camera, given three unit vectors.
     * @param pointX - Unit vector in x direction (right)
     * @param pointY - Unit vector in y direction (up)
     * @param pointZ - Unit vector in z direction (forward)
     */
    public void setCamAxes(Point3D pointX, Point3D pointY, Point3D pointZ) {
        // x axis
        camToWorld.set(0,0,pointX.getX());
        camToWorld.set(0,1,pointX.getY());
        camToWorld.set(0,2,pointX.getZ());

        // y axis
        camToWorld.set(1,0,pointY.getX());
        camToWorld.set(1,1,pointY.getY());
        camToWorld.set(1,2,pointY.getZ());

        // z axis
        camToWorld.set(2,0,pointZ.getX());
        camToWorld.set(2,1,pointZ.getY());
        camToWorld.set(2,2,pointZ.getZ());
    }




    /**
     * Recenters the camera so that it looks at the obj, depend on position of cam, and position of
     * obj.
     * TODO: Fix the recentering
     * @param obj
     */
    public void recenterTo(Object3D obj) {
        // find unit vector between obj.pos and cam.pos
        recenterTo(obj.position);
    }

    public void recenterTo(Point3D position) {
        // if position = camPos then we do nothing
        if (position.equals(camPos)) {
            return;
        }
        Point3D objToCam = this.camPos.subtract(position);
        forward = objToCam.normalize(); // change direction of forward here

        // change cam space unit vectors accordingly

        Point3D temp = new Point3D(0,1,0);

        right = temp.crossProduct(forward);
        up = forward.crossProduct(right);

        // not sure about these changes
        forward = forward.multiply(-1);
        setCamAxes(right, up, forward);
    }

    public void roll(double angle) {

    }

    /**
     * Move cam from current position to new position relative to current
     * @param point - Position relative to the position of the cam
     */
    public void moveCam(Point3D point) {
        setPos(camPos.add(point));
    }

    public Point3D getCamPos() {
        return camPos;
    }

}
