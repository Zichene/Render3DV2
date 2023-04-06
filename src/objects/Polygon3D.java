package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;

public abstract class Polygon3D {

    public Point3D position;
    public Point3D[] points;
    private Color color = new Color(100, 100, 100 ,60); // default color
    public Polygon3D(Point3D position, Point3D[] points) {

        this.position = position;
        this.points = points;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
    /**
     * Initializes an object using the graphics panel as parameter.
     */

    public boolean initObj(GraphicsPanel g, Object3D o) {
        return g.initShape(o.toWorldCoordinates(points), getColor(), true);
    }
}
