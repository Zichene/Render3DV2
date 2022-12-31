package objects;


import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;
import java.util.ArrayList;

public abstract class Object3D {

    public ArrayList<Polygon3D> faces;
    public ArrayList<Point3D> vertices;

    public Point3D position;

    public Object3D(ArrayList<Polygon3D> faces, ArrayList<Point3D> vertices, Point3D position) {
        this.position = position;
        this.faces = faces;
        this.vertices = vertices;
    }

    public abstract boolean initObj(GraphicsPanel g);

}
