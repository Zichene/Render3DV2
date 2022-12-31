package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

public abstract class Polygon3D {

    public Point3D position;
    public Point3D[] points;

    public Polygon3D(Point3D position, Point3D[] points) {

        this.position = position;
        this.points = points;
    }



    /**
     * Initializes an object using the graphics panel as parameter.
     */

    public abstract boolean initObj(GraphicsPanel g);
}
