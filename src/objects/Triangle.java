package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;

public class Triangle extends Polygon3D{

    Point3D[] points;

    public Triangle(Point3D position, Point3D[] points) {
        // default position of triangle 0,0,0
        super(position, points);
        if (points.length != 3) {
            throw new IllegalArgumentException("Need 3 points for Triangle constructor");
        }
        this.points = points;
        this.position = findCentroid(points);
    }

    public Point3D findCentroid(Point3D[] pts) {
        if (pts.length != 3) {
            throw new IllegalArgumentException("Invalid number of points passed to Triangle constructor");
        }
        double xave = (pts[0].getX() + pts[1].getX() + pts[2].getX())/3.0;
        double yave = (pts[0].getY() + pts[1].getY() + pts[2].getY())/3.0;
        double zave = (pts[0].getZ() + pts[1].getZ() + pts[2].getZ())/3.0;
        return new Point3D(xave,yave,zave);
    }
}
