package gui;

import javafx.geometry.Point3D;
import objects.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ObjFileReader {

    ArrayList<Point3D> vertices;

    ArrayList<Polygon3D> faces;

    double scaleUpFactor = 1;

    public Object3D obj;

    // Assume all faces are triangles
    public ObjFileReader(File file, int numOfVertices, int numOfFaces) throws FileNotFoundException {
        if (!file.getName().endsWith(".obj")) {
            throw new FileNotFoundException("Invalid file type (required .obj)");
        }
        Scanner scanner = new Scanner(file);
        vertices = new ArrayList<>(numOfVertices);
        faces = new ArrayList<>(numOfFaces);
        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            if (nextLine.startsWith("v ")) {
                // vertex
                String[] args = nextLine.split(" ");
                args = removeSpaces(args);
       //         System.out.println(Arrays.toString(args));  // DEBUG
                double x = scaleUpFactor*Double.parseDouble(args[1]);
                double y = scaleUpFactor*Double.parseDouble(args[2]);
                double z = scaleUpFactor*Double.parseDouble(args[3]);
                vertices.add(new Point3D(x,y,z));
            } else if (nextLine.startsWith("f ")) {
                // faces
                String[] args = nextLine.split(" ");
                args = removeSpaces(args);
                if (args.length == 4) {
                    // triangles
                    int p1 = Integer.parseInt(args[1]);
                    int p2 = Integer.parseInt(args[2]);
                    int p3 = Integer.parseInt(args[3]);
                    Point3D[] pts = {vertices.get(p1-1), vertices.get(p2-1), vertices.get(p3-1)};
                    faces.add(new Triangle(new Point3D(0,0,0), pts));
                } else {
                    // higher level polygon
                    Point3D[] pts = new Point3D[args.length - 1];
                    for (int i = 0; i < args.length-1; i++) {
                        pts[i] = vertices.get(Integer.parseInt(args[i+1])-1);
                    }
                    faces.add(new PolyG(new Point3D(0,0,0), pts));
                }
            }
        }
    }

    private String[] removeSpaces(String[] input) {
        int curIndex = 0;

        for (String s : input) {
            if (!s.equals("")) {
                curIndex++;
            }
        }
        String[] res = new String[curIndex];
        curIndex = 0;
        for (String s: input)  {
            if (!s.equals("")) {
                res[curIndex] = s;
                curIndex++;
            }
        }
    return res;
    }

    public ArrayList<Point3D> getVertices() {
        return vertices;
    }

    public ArrayList<Polygon3D> getFaces() {
        return faces;
    }

    public Object3D getObject() {
        return new CustomObject(faces, vertices, new Point3D(0,0,0));
    }

}
