package graphics;

import Jama.Matrix;
import javafx.geometry.Point3D;

import java.awt.*;

public class Renderer {

    private Camera cam;

    // near clipping plane
    private double DIST_TO_CANVAS;

    private static boolean DEBUG = false; // shows which points are not visible

    public Renderer() {

        cam = new Camera();
        DIST_TO_CANVAS = cam.nearClipPlane;
    }

    /**
     * Converts a point in World Space, into screen coordinates, depending on position of
     * the camera and the width and height of the screen.
     * @param point
     * @return
     */
    public Point convertToScreenCoordinates(Point3D point, double screenWidth, double screenHeight) {
        // first convert point to camera space
        Point3D point_cam = toCameraSpace(point);
        // perspective divide (assuming canvas is 1 unit away from camera)
        Point3D perspDiv = new Point3D(DIST_TO_CANVAS*point_cam.getX()/(point_cam.getZ()), DIST_TO_CANVAS*point_cam.getY()/(point_cam.getZ()), point_cam.getZ());
        if (!isVisible(perspDiv)) {
            return null;
        }
        return toPixelCoordinates(perspDiv, screenWidth,screenHeight);
    }

    public Point[] convertToScreenCoordinates(Point3D[] points, double screenWidth, double screenHeight) {
        Point[] result = new Point[points.length];
        for (int i = 0; i < points.length; i++) {
            result[i] = convertToScreenCoordinates(points[i], screenWidth, screenHeight);
            // want to break immediately when we find a null point, meaning whole shape will not be displayed
            if (result[i] == null) {
                return null;
            }
        }
        return result;
    }

    /**
     * Checks if the projected point is visible or not (if it within the canvas and if it is
     * in front of the camera.
     * @param point
     * @return
     */
    private boolean isVisible(Point3D point) {
        // after getting projected onto nearClipPlane, we keep the z coord, and check if
        // the z coord was between the near and far clip planes and if the point is within the canvas

        return point.getZ() >= cam.nearClipPlane && point.getZ() <= cam.farClipPlane && ((Math.abs(point.getX()) <= cam.CANVAS_W/2.0)
                && (Math.abs(point.getY()) <= cam.CANVAS_H/2.0));
    }

    private Point3D toCameraSpace(Point3D pt) {
        double[][] pointMat = {{pt.getX(), pt.getY(), pt.getZ(), 1}};
        Matrix pt_wldSpace = new Matrix(pointMat);
        Matrix pt_camSpace = pt_wldSpace.times(cam.getWorldToCam());
        return new Point3D(pt_camSpace.getRowPackedCopy()[0],
                pt_camSpace.getRowPackedCopy()[1], pt_camSpace.getRowPackedCopy()[2]);
    }

    private Point toPixelCoordinates(Point3D pt, double screenWidth, double screenHeight) {
        // normalize pt
        double x_norm = (pt.getX() + (cam.CANVAS_W/2.0))/(cam.CANVAS_W);
        double y_norm = (pt.getY() + (cam.CANVAS_H/2.0))/(cam.CANVAS_H);

        return new Point((int) (Math.floor(x_norm*(screenWidth))),
                (int) (Math.floor((1-y_norm)*screenHeight)));

    }

    public Camera getCam() {
        return cam;
    }

    public void setCamPos(Point3D point) {
        cam.setPos(point);
    }

    public double getDIST_TO_CANVAS() {
        return DIST_TO_CANVAS;
    }

}
