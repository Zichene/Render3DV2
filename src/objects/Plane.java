package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;

public class Plane extends Object3D{

    Point3D[] plane;

    static int planeSize = 5;

    static Color planeColor = Color.LIGHT_GRAY;

    /**
     * Plane parallel to x-z plane.
     * @param position
     */
    public Plane(Point3D position) {
        super(position);
        Point3D p1 = position.add(new Point3D(planeSize, 0, planeSize));
        Point3D p2 = position.add(new Point3D(-planeSize, 0, planeSize));
        Point3D p3 = position.add(new Point3D(-planeSize, 0, -planeSize));
        Point3D p4 = position.add(new Point3D(planeSize, 0, -planeSize));
        plane = new Point3D[]{p1, p2, p3, p4};
    }

    @Override
    public boolean initObj(GraphicsPanel g) {
        // make more transparent
        Color color = planeColor;
        color = new Color(color.getRed(), color.getBlue(), color.getGreen(), 60);

        return g.initShape(plane, color, true);
    }

    public String toString() {
        return "Plane ID: " + hashCode();
    }
}
