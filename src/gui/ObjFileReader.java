package gui;

import javafx.geometry.Point3D;
import objects.*;

import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ObjFileReader {

    ArrayList<Point3D> vertices;
    ArrayList<Polygon3D> faces;
    double scaleUpFactor = 1;

    public Object3D obj;

    // Assume all faces are triangles
    public ObjFileReader(File file) throws FileNotFoundException, IndexOutOfBoundsException {
        if (!file.getName().endsWith(".obj")) {
            throw new FileNotFoundException("Invalid file type (required .obj)");
        }
        Scanner scanner = new Scanner(file);
        vertices = new ArrayList<>();
        faces = new ArrayList<>();
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
                // in .obj files faces may be formatted as 1/1/1, 2/2/2, 3/3/3, 4/4/4, etc.
                removeSlashes(args);
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

    private void removeSlashes(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String[] split = args[i].split("/");
            // the zeroth index are the face coordinates
            args[i] = split[0];
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

    public Object3D getObject() {
        return new CustomObject(faces.toArray(new Polygon3D[0]), vertices.toArray(new Point3D[0]), new Point3D(0,0,0));
    }

}
