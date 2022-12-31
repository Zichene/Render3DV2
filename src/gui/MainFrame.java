package gui;

import graphics.Camera;
import graphics.CameraPanel;
import graphics.GraphicsPanel;
import javafx.geometry.Point3D;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import objects.*;

/**
 * This class implements a JFrame on which we display the graphics panel and other panels
 * The position and orientation of the camera can be changed to obtain different views of one
 * 3D object using arrow keys (to move the camera relative to itself), and the mouse (to recenter the camera
 * so that it faces towards the center of the displayed obj).
 */
public class MainFrame extends JFrame implements KeyListener, MouseListener {

    // Class fields / parameters
    private static final int SCREEN_WIDTH = 750;
    private static final int SCREEN_HEIGHT = 700; // used to be 650

    private GraphicsPanel panel;

    private CameraPanel cpanel;

    private JPanel mainPanel;

    private JScrollPane logPanel;

    private JTextPane textPane;

    private boolean DEBUG = false; // shows camera position in console

    private boolean MOVE_RELATIVE_TO_CAM = true;

    private boolean AUTO_RECENTER = true;

    private boolean SHOW_TIME_INFO = true;

    /**
     * Constructor, creates a window (JFrame) with a title. The size of the window is set to SCREEN_WIDTH * SCREEN_HEIGHT.
     *
     * @param title
     */
    public MainFrame(String title) {

        super(title);
        // change all fonts to custom font
        changeAllFonts();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setBackground(Color.LIGHT_GRAY);
        setVisible(true);
        setResizable(false);  // resizable
        addKeyListener(this);
        addMouseListener(this);

        // MenuBar
        MainMenuBar menuBar = new MainMenuBar(this);
        this.setJMenuBar(menuBar);

        // main panel
        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        mainPanel.setLocation(0, menuBar.getHeight());
        mainPanel.setBackground(Color.LIGHT_GRAY);
        this.add(mainPanel);

        // graphics panel
        panel = new GraphicsPanel();
        panel.setVisible(true);
        // panel size: 500 x 500
        panel.setLocation((SCREEN_WIDTH - panel.SCREEN_WIDTH) / 2, 100);

        // camera panel
        cpanel = new CameraPanel(getCam());
        cpanel.setVisible(true);
        cpanel.setLocation((panel.getX() - cpanel.SCREEN_WIDTH) / 2, (panel.getX() - cpanel.SCREEN_WIDTH) / 2);

        // reset panel y
        panel.setLocation((SCREEN_WIDTH - panel.SCREEN_WIDTH) / 2, cpanel.getY());

        // text panel
        textPane = new JTextPane();
        textPane.setBounds(panel.getX(), 550, 500, 5000);
        textPane.setVisible(true);
        textPane.setLayout(null);
        textPane.setEditable(false);
        textPane.setFocusable(false);

        // scrollpane
        logPanel = new JScrollPane(textPane);
        logPanel.setBounds(panel.getX(), 525, 500, 75);
        logPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logPanel.setAutoscrolls(true);
        getContentPane().setLayout(null);
        getContentPane().add(logPanel);
        logPanel.setViewportView(textPane);
        logPanel.setBorder(new LineBorder(Color.BLACK, 2));

        // add all panels to mainPanel
        mainPanel.add(panel);
        mainPanel.add(cpanel);
 //       mainPanel.add(textArea);
        mainPanel.add(logPanel);
        mainPanel.setVisible(true);

        // initial message
        initMessage();
        // add this otherwise graphics will not initialize correctly
        cpanel.repaint(10000);
        panel.repaint(10000);
        mainPanel.repaint(10000);
        menuBar.revalidate();
    }

    public GraphicsPanel getGPanel() {
        return panel;
    }

    public Camera getCam() {
        return panel.getRenderer().getCam();
    }

    /**
     * Set which object is to be the displayed obj (the one which we can move the camera about).
     * Also adds the obj to the list of objs in the CameraPanel.
     *
     * @param obj
     */
    public void addDisplayedObj(Object3D obj) {
        panel.addObject(obj);
        cpanel.addObject(obj);
        // immediately render on screen
        update();
    }

    /**
     * This method is called whenever we move the object or the camera to update the rendered image.
     */
    public void update() {
        panel.updatePanel(this);
        cpanel.updatePanel();
        if (DEBUG) {
            printCamPos();
        }
    }

    private void printCamPos() {
        double x = round2Dec(getCam().getCamPos().getX());
        double y = round2Dec(getCam().getCamPos().getY());
        double z = round2Dec(getCam().getCamPos().getZ());
        consoleWrite(" [DEBUG] Position of camera: " + "x = " + x + ", y = " + y + ", z = " + z +"\n");
        //    camPosTextLabel.setBounds(50, 100, 500, 40);
    }

    private double round2Dec(double num) {
        return Math.round(num * 100.0) / 100.0;
    }

    public void consoleWrite(String text) {
        textPane.setForeground(Color.RED);
        textPane.setText(textPane.getText() + " " + text);
    }

    public boolean toggleDebug() {
        DEBUG = !DEBUG;
        return DEBUG;
    }

    public boolean toggleMoveRelativeToCam() {
        MOVE_RELATIVE_TO_CAM = !MOVE_RELATIVE_TO_CAM;
        return MOVE_RELATIVE_TO_CAM;
    }

    public boolean toggleAutoRecenter() {
        AUTO_RECENTER = !AUTO_RECENTER;
        return AUTO_RECENTER;
    }

    public boolean toggleTimeInfo() {
        SHOW_TIME_INFO = !SHOW_TIME_INFO;
        return SHOW_TIME_INFO;
    }

    /**
     * Loads an object (.obj file) on the screen using JFileChooser
     */
    public void loadObject(boolean preload) {
        JFileChooser jfc = new JFileChooser();
        if (preload) {
            jfc.setCurrentDirectory(new File("src/resources/objs"));
        }
        switch (jfc.showOpenDialog(this)) {
            case JFileChooser.APPROVE_OPTION:
                File file = new File(jfc.getSelectedFile().getAbsolutePath());
                loadCustomObject(file);
                break;
        }
    }

    public void loadCustomObject(File file) {
        // can only load one obj at a time for now, clear previous obj
        clearAll();
        ObjFileReader objFileReader = null;
        try {
            objFileReader = new ObjFileReader(file, 2500, 2400);
        } catch (FileNotFoundException e) {
            consoleWrite("[System] No file found / invalid file type (.obj required).\n");
            throw new RuntimeException(e);
        }
        this.addDisplayedObj(objFileReader.getObject());
        consoleWrite("[System] Successfully imported " + file.getName() + "\n");
    }

    public void clearAll() {
        panel.clearShapes();
        cpanel.clearShapes();

        panel.repaint();
        cpanel.repaint();

        panel.removeObjects();
        cpanel.removeObjects();
    }

    /**
     * Initial message to user
     */
    private void initMessage() {
        double x = round2Dec(getCam().getCamPos().getX());
        double y = round2Dec(getCam().getCamPos().getY());
        double z = round2Dec(getCam().getCamPos().getZ());
        consoleWrite("[System] " + "Welcome to Render3D. You may move around the camera" +
                " with the arrow keys and the space and shift keys to go down (note: you cannot hold " +
                "the shift key). Click the mouse to recenter camera to the object." + "\n");
        consoleWrite("[System] Initial camera position: " +
                "x = " + x + ", y = " + y + ", z = " + z + "\n");
    }


    /**
     * OVERRIDEN METHODS FROM IMPLEMENTING KeyListener and MouseListener
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (MOVE_RELATIVE_TO_CAM) {
                    getCam().moveCam(getCam().right.multiply(-1));
                } else {
                    getCam().moveCam(new Point3D(-1, 0, 0));
                }
                break;

            case KeyEvent.VK_RIGHT:
                if (MOVE_RELATIVE_TO_CAM) {
                    getCam().moveCam(getCam().right);
                } else {
                    getCam().moveCam(new Point3D(1, 0, 0));
                }
                break;

            case KeyEvent.VK_UP:
                if (MOVE_RELATIVE_TO_CAM) {
                    getCam().moveCam(getCam().forward);
                } else {
                    getCam().moveCam(new Point3D(0, 0, -1));
                }
                break;

            case KeyEvent.VK_DOWN:
                if (MOVE_RELATIVE_TO_CAM) {
                    getCam().moveCam(getCam().forward.multiply(-1));
                } else {
                    getCam().moveCam(new Point3D(0, 0, 1));
                }
                break;

            case KeyEvent.VK_SPACE:
                if (MOVE_RELATIVE_TO_CAM) {
                    getCam().moveCam(getCam().up);
                } else {
                    getCam().moveCam(new Point3D(0, 1, 0));
                }
                break;

            case KeyEvent.VK_SHIFT:
                if (MOVE_RELATIVE_TO_CAM) {
                    getCam().moveCam(getCam().up.multiply(-1));
                } else {
                    getCam().moveCam(new Point3D(0, -1, 0));
                }
                break;
        }

        if (AUTO_RECENTER && (panel.getObjects().size() != 0)) {
            // RECENTER TO FIRST OBJ
            getCam().recenterTo(panel.getObjects().get(0));
        }
        update();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (panel.getObjects().size() == 0) {
            return;
        }
        getCam().recenterTo(panel.getObjects().get(0));
   //     updateDisplayedObj();
        update();
        consoleWrite("[System] Camera recentered to" + panel.getObjects().get(0) + "\n");
    }

    public boolean showTimeInfo() {
        return SHOW_TIME_INFO;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    
    private void changeAllFonts() {
        try {
            Font f = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("src/resources/fonts/Aldo semi-bold0.12.ttf"));
            f = f.deriveFont(15.0F);
            UIManager.put("Button.font", f);
            UIManager.put("ToggleButton.font", f);
            UIManager.put("RadioButton.font", f);
            UIManager.put("CheckBox.font", f);
            UIManager.put("ColorChooser.font", f);
            UIManager.put("ComboBox.font", f);
            UIManager.put("Label.font", f);
            UIManager.put("List.font", f);
            UIManager.put("MenuBar.font", f);
            UIManager.put("MenuItem.font", f);
            UIManager.put("RadioButtonMenuItem.font", f);
            UIManager.put("CheckBoxMenuItem.font", f);
            UIManager.put("Menu.font", f);
            UIManager.put("PopupMenu.font", f);
            UIManager.put("OptionPane.font", f);
            UIManager.put("Panel.font", f);
            UIManager.put("ProgressBar.font", f);
            UIManager.put("ScrollPane.font", f);
            UIManager.put("Viewport.font", f);
            UIManager.put("TabbedPane.font", f);
            UIManager.put("Table.font", f);
            UIManager.put("TableHeader.font", f);
            UIManager.put("TextField.font", f);
            UIManager.put("PasswordField.font", f);
            UIManager.put("TextArea.font", f);
            UIManager.put("TextPane.font", f);
            UIManager.put("EditorPane.font", f);
            UIManager.put("TitledBorder.font", f);
            UIManager.put("ToolBar.font", f);
            UIManager.put("ToolTip.font", f);
            UIManager.put("Tree.font", f);
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
