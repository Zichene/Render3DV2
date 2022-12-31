package objects;

import graphics.GraphicsPanel;
import javafx.geometry.Point3D;
import objects.Object3D;

import java.awt.*;

public class Cube {

    public Point3D[] points;
    public double size;
    public Color color;

    public Point3D center;

    public boolean isFilled;

    public Cube(double size, Point3D center, Color color, boolean isFilled) {

        this.size = size;
        this.color = color;
        this.isFilled = isFilled;
        this.center = center;

        if (size <= 0) {
            throw new IllegalArgumentException("Cannot create cube with 0 or less size");
        }
        // front 4 points in CCW order, starting from bottom left
        Point3D f1 = new Point3D(center.getX()-size/2, center.getY()-size/2, center.getZ()+size/2);
        Point3D f2 = new Point3D(center.getX()+size/2, center.getY()-size/2, center.getZ()+size/2);
        Point3D f3 = new Point3D(center.getX()+size/2, center.getY()+size/2, center.getZ()+size/2);
        Point3D f4 = new Point3D(center.getX()-size/2, center.getY()+size/2, center.getZ()+size/2);

        // back 4 points in CCW order, starting from bottom left
        Point3D b1 = new Point3D(center.getX()-size/2, center.getY()-size/2, center.getZ()-size/2);
        Point3D b2 = new Point3D(center.getX()+size/2, center.getY()-size/2, center.getZ()-size/2);
        Point3D b3 = new Point3D(center.getX()+size/2, center.getY()+size/2, center.getZ()-size/2);
        Point3D b4 = new Point3D(center.getX()-size/2, center.getY()+size/2, center.getZ()-size/2);

        points = new Point3D[]{f1, f2, f3, f4, b1, b2, b3, b4};

    }

    public Point3D[] getFrontFace() {
        return new Point3D[]{points[0], points[1], points[2],points[3]};
    }

    public Point3D[] getBackFace() {
        return new Point3D[]{points[4], points[5], points[6],points[7]};
    }

    public Point3D[] getTopFace() {
        return new Point3D[]{points[2], points[3], points[7],points[6]};
    }

    public Point3D[] getBottomFace() {
        return new Point3D[]{points[0], points[1], points[5], points[4]};
    }

    public Point3D[] getLeftFace() {
        return new Point3D[]{points[0], points[3], points[7],points[4]};
    }

    public Point3D[] getRightFace() {
        return new Point3D[]{points[1], points[2], points[6],points[5]};
    }

    public Point3D getCenter() {
        return center;
    }

    public Cube initObj(double size, Point3D center, Color color, boolean filled, GraphicsPanel g) {
        Cube cube = new Cube(size, center, color, true);

        // init cube
        initObj(g);
        return cube;
    }

    public boolean initObj(GraphicsPanel g) {

        Color color = this.color;
        color = new Color(color.getRed(), color.getBlue(), color.getGreen(), 60);

        // init all faces
        return g.initShape(this.getFrontFace(), color, this.isFilled) &&
        g.initShape(this.getBackFace(), color, this.isFilled) &&
        g.initShape(this.getTopFace(), color, this.isFilled) &&
        g.initShape(this.getBottomFace(), color, this.isFilled) &&
        g.initShape(this.getLeftFace(), color, this.isFilled) &&
        g.initShape(this.getRightFace(), color, this.isFilled);
    }

    /**
     * Changes the position of the cube along with all of its points
     * @param position
     */
    public void moveTo(Point3D position) {
        this.center = position;
        Cube newCube = new Cube(size, position, color, isFilled);
        points = newCube.points;
    }

    public String toString() {
        return " Cube ID: " + hashCode();
    }

}
