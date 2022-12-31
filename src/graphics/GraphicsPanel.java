package graphics;

import gui.MainFrame;
import javafx.geometry.Point3D;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import objects.*;

/**
 * This class implements a JPanel on which we draw projected 3D objects
 * using colored shapes.
 */
public class GraphicsPanel extends JPanel {

    public int SCREEN_WIDTH = 500;
    public int SCREEN_HEIGHT = 500;

    private ArrayList<ColoredShape> shapes;
    private Renderer renderer;

    private ArrayList<Object3D> objects;

    /**
     * Constructor, creates a new GraphicsPanel object, with a defined SCREEN_WIDTH and SCREEN_HEIGHT.
     * The background color is white by default.
     */
    public GraphicsPanel() {

        super();
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setBackground(Color.WHITE);
        setVisible(false);
        setBorder(new LineBorder(Color.BLACK, 2, false));
        setFocusable(true);

        shapes = new ArrayList<>();
        renderer = new Renderer();
        objects = new ArrayList<>();
    }

    /**
     * Initializes a 3D point given in WORLD COORDINATES on the screen.
     * The color is by default black.
     * @param point - 3D point in world coordinates
     * @param size - size of the point, in pixels
     */
    public void initPoint(Point3D point, int size) {
        Point pt = renderer.convertToScreenCoordinates(point, SCREEN_WIDTH, SCREEN_HEIGHT);
        Point[] points = {pt, new Point((int) (pt.getX()+size), (int) pt.getY()),
                new Point((int) (pt.getX()+size), (int) (pt.getY()+size)), new Point((int) pt.getX(), (int) (pt.getY()+size))};
        System.out.println(Arrays.toString(points));
        initShape(points, Color.BLACK, true);
    }

    /**
     * Initializes a shape using points in SCREEN COORDINATES (2D points)
     * @param points - Points in 2D screen coordinates
     * @param color - Color of the shape
     * @param isFilled - Boolean indicating whether the shape is to be filled
     */
    public void initShape(Point[] points, Color color, boolean isFilled) {
        shapes.add(new ColoredShape(points, color, isFilled));
    }

    /**
     * Initializes a shape using points in WORLD COORDINATES (3D points)
     * @param points - Points in 3D world coordinates
     * @param color - Color of the shape
     * @param isFilled - Boolean indicating whether the shape is to be filled
     * @return Boolean indicating whether the shape is fully visible to camera
     */
    public boolean initShape(Point3D[] points, Color color, boolean isFilled) {
        Point[] points2D = renderer.convertToScreenCoordinates(points, SCREEN_WIDTH, SCREEN_HEIGHT);
        if (!(points2D == null)) {
                // only add if all points are visible
                shapes.add(new ColoredShape(points2D, color, isFilled));
                return true;
        } else {
            return false;
        }
        // return isFullyVisible() induces alot more lag.
    }

    /**
     * Checks if a shape is fully visible to the camera. Goes through the shapes arraylist
     * and checks if all of them are fully visible. TODO: This adds alot of lag to the rendering.
     * @return
     */
    public boolean isFullyVisible() {
        boolean fullyVisible = true;
        for (ColoredShape s : shapes) {
            if (!s.fullyVisible) {
                fullyVisible = false;
                break;
            }
        }
        return fullyVisible;
    }

    /**
     * Adds a Object3D to the list of objects to be rendered.
     * @param obj
     */
    public void addObject(Object3D obj) {
        objects.add(obj);
    }

    /**
     * Method renders all the objects on the screen, in the order such that the
     * closest points to the camera are rendered first. To be used when solid objects
     * are involved.
     */
    private void render(JTextPane tp) {
        // sort all objs in objects arraylist
        HashMap<Double, Object3D> map = new HashMap<Double, Object3D>();
        for (Object3D obj : objects) {
            double distance = obj.position.distance(renderer.getCam().getCamPos());
            map.put(distance, obj);
        }

        Object[] arr = map.keySet().toArray();
        Arrays.sort(arr);

        // closest objects are rendered last!
        for (int i = arr.length-1; i >= 0; i--) {
            if (!map.get(arr[i]).initObj(this)) {
                tp.setText(tp.getText() + "\n [SYSTEM] " + map.get(arr[i]) + " is not fully visible to camera.");
            }
        }


    }

    private void renderSimple(MainFrame f) {
        long t1 = System.currentTimeMillis();
        for (Object3D obj : objects) {
            obj.initObj(this);
            // THIS CODE MAKES IT LAG when objs are not visible to cam
       //     if (!obj.initObj(this)) {
       //         tp.setText(tp.getText() + "\n [SYSTEM] " + obj + " is not fully visible to camera.");
       //     }
            long t2 = System.currentTimeMillis();
            if (obj.faces.size() != 0 && f.showTimeInfo()) {
                f.consoleWrite("[System] Time taken to render " + obj.faces.size() + " polygons :" + (t2-t1) + " ms.\n");
                double tpo = (t2-t1)/(double)(obj.faces.size());
                tpo = Math.round(tpo * 10000.0)/10000.0;
                f.consoleWrite("[System] Average render time per polygon: " +  tpo + " ms/obj.\n");
            }
        }

    }

    public void updatePanel(MainFrame f) {
        clearShapes();
        repaint();
       // render(tp);
        renderSimple(f);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // NOTE: using ITERATOR was giving some kind of concurrentmodificationexp, fixed with normal
        // for loop
        for (int i = 0; i < shapes.size(); i++) {
            ColoredShape s = shapes.get(i);
            g2.setColor(s.getColor());
            g2.draw(s.outline);
            if (s.isFilled)
                g2.fill(s.outline);
        }
    }

    public Renderer getRenderer(){
        return renderer;
    }

    /**
     * Clears the ArrayList containing the current shapes to be drawn on screen.
     * @return - The shapes that have just been cleared.
     */
    public ArrayList<ColoredShape> clearShapes() {
        ArrayList<ColoredShape> tmp = shapes;
        shapes = new ArrayList<>();
        return tmp;
    }

    public ArrayList<ColoredShape> getShapes() {
        return shapes;
    }

    public ArrayList<Object3D> getObjects() {
        return objects;
    }

    public void removeObjects() {
        objects = new ArrayList<>();
    }

    /**
     * Class representing a colored shape to be displayed on screen.
     */
    class ColoredShape {
        Polygon outline;
        Color color;
        boolean isFilled;

        boolean fullyVisible;

        public ColoredShape(Point[] points, Color color, boolean isFilled) {
            fullyVisible = true;
            this.isFilled = isFilled;
            this.color = color;
            outline = new Polygon();
            for (Point p : points) {
                if (p != null) {
                    outline.addPoint((int) p.getX(), (int) p.getY());
                } else {
                    fullyVisible = false;
                }
            }
        }

        public Polygon getOutline() {
            return outline;
        }

        public Color getColor() {
            return color;
        }

    }
}


