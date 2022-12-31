package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;

public class PolyG extends Polygon3D {
    public PolyG(Point3D position, Point3D[] points) {
        super(position, points);
    }

    @Override
    public boolean initObj(GraphicsPanel g) {
        Color color = new Color(0, 100, 0, 60);
        return g.initShape(points, color, true);
    }
}
