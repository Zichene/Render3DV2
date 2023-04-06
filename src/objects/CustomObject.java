package objects;

import Jama.Matrix;
import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CustomObject extends Object3D{
    public CustomObject(Polygon3D[] faces, Point3D[] vertices, Point3D position) {
        super(faces, vertices, position);
    }

    @Override
    public boolean initObj(GraphicsPanel g) {
        for (int i = 0; i < faces.length; i++) {
            faces[i].setColor(getColor());
            faces[i].initObj(g, this);
        }
        return false; // not implemented boolean correctly
    }


    /**
     * To be called when we care about which polygons to render first (those closest to camera)
     * 
     * @param g
     * @return
     */
    public boolean initObjSolid(GraphicsPanel g) {
        HashMap<Double, Polygon3D> map = new HashMap<>();
        for (Polygon3D obj : faces) {
            double distance = obj.position.distance(g.getRenderer().getCam().getCamPos());
            map.put(distance, obj);
        }
        Object[] arr = map.keySet().toArray();
        Arrays.sort(arr);
        // closest objects are rendered last!
        for (int i = arr.length-1; i >= 0; i--) {
            map.get(arr[i]).initObj(g, this);
        }
        return true;
    }

    /**
     * Shifts all the faces by a point "position".
     * NOTE: the position of the vertices themselves are not changed, only the points making up
     * the polygons.
     * @param position
     */
    public void moveObj(Point3D position) {
        this.position = position;
        // change objtoworld matrix
        objectToWorld.set(3,0,position.getX());
        objectToWorld.set(3,1,position.getY());
        objectToWorld.set(3,2,position.getZ());
    }

    // rotate about "up" axis
    public void rotateObj(double angle) {
        angle = Math.toRadians(angle);
        double cos = Math.cos(angle);
        double sin  = Math.sin(angle);
        double rightnewx = cos*right.getX() - sin*right.getZ();
        double rightnewz = sin*right.getX() + cos*right.getZ();
        right = new Point3D(rightnewx, right.getY(), rightnewz);
        forward = up.crossProduct(right);
        setAxes(right, up, forward);
    }
}
