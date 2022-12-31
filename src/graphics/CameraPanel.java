package graphics;

import javafx.geometry.Point3D;
import objects.Object3D;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

public class CameraPanel extends GraphicsPanel {


    private Camera camera;

    private Point camPos2D;

    private Point camRight;

    private Point camForward;

    static final int ARROW_SCALE_UP = 10;

    private ArrayList<Object3D> objs;

    private static boolean DEBUG = false; // prints 2D camera orientation

    /**
     * This class creates a panel which displays the position and orientation of the camera in
     * 2D.
     * @param camera
     */
    public CameraPanel(Camera camera) {

        super();
        SCREEN_HEIGHT = 100;
        SCREEN_WIDTH = 100;
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setBackground(Color.WHITE);
        setVisible(false);
        setBorder(new LineBorder(Color.BLACK, 2, false));

        // initialize position of cam
        camPos2D = new Point((int) Math.round(camera.getCamPos().getX()), (int) Math.round(camera.getCamPos().getZ()));
        initPoint(camPos2D, 1, Color.BLUE);

        // initialize x and y unit vectors, scale up by amount
        camRight = new Point((int) Math.round(ARROW_SCALE_UP*camera.right.getX()),
                (int) Math.round(ARROW_SCALE_UP*camera.right.getZ()));

        camForward = new Point((int) Math.round(ARROW_SCALE_UP *camera.forward.getX()),
                (int) Math.round(-ARROW_SCALE_UP*camera.forward.getZ()));

        initArrow(camPos2D, camRight, Color.RED);
        initArrow(camPos2D, camForward, Color.GREEN);

        this.camera = camera;
        objs = new ArrayList<>();

        setToolTipText("Top view of camera and objects. Green arrow is forward direction of camera, " +
                "red arrow is the right of the camera");
    }

    /**
     * Initializes a point on the screen relative to a center point
     * @param pt
     * @param size
     */
    public void initPoint(Point pt, int size, Color color) {
        // centering point relative to origin
        pt.translate(SCREEN_WIDTH/2, SCREEN_HEIGHT/2 + 20);

        Point[] points = {pt, new Point((int) (pt.getX()+size), (int) pt.getY()),
                new Point((int) (pt.getX()+size), (int) (pt.getY()+size)), new Point((int) pt.getX(), (int) (pt.getY()+size))};
        initShape(points, color, true);
    }

    public void initArrow(Point origin, Point vector, Color color) {
        // draw line from origin to vector
        Point end = new Point(origin.x + vector.x, origin.y - vector.y);
        Point[] line = {origin, end};
        initShape(line, color, false);

        // draw triangle on head of arrow

    }

    public void addObject(Object3D obj) {
        objs.add(obj);
        // display the center of this new obj
        initPoint(new Point((int) Math.floor(obj.position.getX()), (int)Math.floor(obj.position.getZ())), 1, Color.BLACK);
    }

    public void removeObjects() {
        objs = new ArrayList<>();
    }

    public void updatePanel() {
        clearShapes();
        repaint();
        // get new CamPos
        camPos2D = new Point((int) Math.round(camera.getCamPos().getX()), (int) Math.round(camera.getCamPos().getZ()));
        initPoint(camPos2D, 1, Color.BLUE);
        // update the arrows
        camRight = new Point((int) Math.round(ARROW_SCALE_UP*camera.right.getX()),
                (int) Math.round(-ARROW_SCALE_UP*camera.right.getZ()));

        camForward = new Point((int) Math.round(ARROW_SCALE_UP*camera.forward.getX()),
                (int) Math.round(-ARROW_SCALE_UP*camera.forward.getZ()));
        // DEBUG
        if (DEBUG) {
            System.out.println("Cam Pos: " + camPos2D + "\n Cam Right: " + camRight + "\n Cam Forward: " + camForward );
        }
        initArrow(camPos2D, camRight, Color.RED);
        initArrow(camPos2D, camForward, Color.GREEN);

        // updates objs
        updateObjects();
    }


    private void updateObjects() {
        for (Object3D obj : objs) {
            initPoint(new Point((int) Math.floor(obj.position.getX()), (int)Math.floor(obj.position.getZ())), 1, Color.BLACK);
        }
    }
}
