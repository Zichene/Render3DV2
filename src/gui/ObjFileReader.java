package gui;

import javafx.geometry.Point3D;
import objects.Triangle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ObjFileReader {

    ArrayList<Point3D> vertices;

    ArrayList<Triangle> tris;

    double scaleUpFactor = 1;

    // Assume all faces are triangles
    public ObjFileReader(File file, int numOfVertices, int numOfFaces) throws FileNotFoundException {
        if (!file.getName().endsWith(".obj")) {
            throw new FileNotFoundException("Invalid file type (required .obj)");
        }
        Scanner scanner = new Scanner(file);
        vertices = new ArrayList<>(numOfVertices);
        tris = new ArrayList<>(numOfFaces);
        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            if (nextLine.startsWith("v ")) {
                // vertex
                String[] args = nextLine.split(" ");
                args = removeSpaces(args, args.length);
       //         System.out.println(Arrays.toString(args));  // DEBUG
                double x = scaleUpFactor*Double.parseDouble(args[1]);
                double y = scaleUpFactor*Double.parseDouble(args[2]);
                double z = scaleUpFactor*Double.parseDouble(args[3]);
                vertices.add(new Point3D(x,y,z));
            } else if (nextLine.startsWith("f ")) {
                // faces
                String[] args = nextLine.split(" ");
                args = removeSpaces(args, args.length);
                if (args.length == 4) {
                    // triangle
                    int p1 = Integer.parseInt(args[1]);
                    int p2 = Integer.parseInt(args[2]);
                    int p3 = Integer.parseInt(args[3]);
                    Point3D[] pts = {vertices.get(p1-1), vertices.get(p2-1), vertices.get(p3-1)};
                    tris.add(new Triangle(pts));
                }
            }
        }
    }

    private String[] removeSpaces(String[] input, int length) {
        int curIndex = 0;
        String[] res = new String[length];
        for (String s : input) {
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

    public ArrayList<Triangle> getTris() {
        return tris;
    }

}
