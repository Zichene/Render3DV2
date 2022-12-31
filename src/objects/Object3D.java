package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;

public abstract class Object3D {

    public Point3D position;

    public Object3D(Point3D position) {
        this.position = position;
    }



    /**
     * Initializes an object using the graphics panel as parameter.
     */

    public abstract boolean initObj(GraphicsPanel g);
}
