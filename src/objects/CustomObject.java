package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.util.ArrayList;

public class CustomObject extends Object3D{
    public CustomObject(ArrayList<Polygon3D> faces, ArrayList<Point3D> vertices, Point3D position) {
        super(faces, vertices, position);
    }

    @Override
    public boolean initObj(GraphicsPanel g) {
        for (Polygon3D face : faces) {
            face.initObj(g);
        }
        return false; // not implemented boolean correctly
    }
}
