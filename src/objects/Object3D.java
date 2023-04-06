package objects;


import Jama.Matrix;
import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import java.awt.*;
import java.util.ArrayList;

public abstract class Object3D {

    public Polygon3D[] faces;

    public Point3D[] vertices;

    public Point3D position;
    public Matrix objectToWorld;
    
    public Point3D up;
    public Point3D right;
    public Point3D forward;

    private Color color = new Color(100, 100, 100 ,60);


    public Object3D(Polygon3D[] faces, Point3D[] vertices, Point3D position) {
        this.position = position;
        this.faces = faces;
        this.vertices = vertices;

        // initialize the object to world matrix, by default should be identity 4x4
        double[][] matArr = {
                {1,0,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,0,0,0}
        };
        objectToWorld = new Matrix(matArr);
        up = new Point3D(0,1,0);
        right = new Point3D(1,0,0);
        forward = new Point3D(0,0,1);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    // transforms points given in object coordinates to world coordinates
    public Point3D[] toWorldCoordinates(Point3D[] points) {
        Point3D[] res = new Point3D[points.length];
        for (int i = 0; i < points.length; i++) {
            double[][] pointMat = {{points[i].getX(), points[i].getY(), points[i].getZ(), 1}};
            Matrix pt_objSpace = new Matrix(pointMat);
            Matrix pt_wldSpace = pt_objSpace.times(objectToWorld);
            res[i] = new Point3D(pt_wldSpace.getRowPackedCopy()[0],
                    pt_wldSpace.getRowPackedCopy()[1], pt_wldSpace.getRowPackedCopy()[2]);
        }
        return res;
    }
    
  
    public void setAxes(Point3D pointX, Point3D pointY, Point3D pointZ) {
            // x axis
            objectToWorld.set(0,0,pointX.getX());
            objectToWorld.set(0,1,pointX.getY());
            objectToWorld.set(0,2,pointX.getZ());
            // y axis
            objectToWorld.set(1,0,pointY.getX());
            objectToWorld.set(1,1,pointY.getY());
            objectToWorld.set(1,2,pointY.getZ());
            // z axis
            objectToWorld.set(2,0,pointZ.getX());
            objectToWorld.set(2,1,pointZ.getY());
            objectToWorld.set(2,2,pointZ.getZ());
    }

    public abstract void rotateObj(double angle);

    public abstract boolean initObj(GraphicsPanel g);


    public abstract void moveObj(Point3D position);
}
