package gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainMenuBar extends JMenuBar implements ActionListener {

    MainFrame f;

    public MainMenuBar(MainFrame f) {
        super();
        this.f = f;
        this.setBackground(Color.GRAY);
        this.setBorder(new LineBorder(Color.BLACK, 1));

        JMenu m1 = new JMenu("Settings");

        JMenuItem m1item1 = new JMenuItem("Toggle debug");
        m1item1.setToolTipText("Shows the position of camera everytime it moves.");
        m1item1.addActionListener(this);

        JMenuItem m1item2 = new JMenuItem("Toggle auto recenter");
        m1item2.setToolTipText("Auto recenters camera to displayed object.");
        m1item2.addActionListener(this);

        JMenuItem m1item3 = new JMenuItem("Toggle move relative to camera");
        m1item3.setToolTipText("Move relative to camera, rather than world coordinates.");
        m1item3.addActionListener(this);

        m1.add(m1item1);
        m1.add(m1item2);
        m1.add(m1item3);

        JMenu m2 = new JMenu("Open");

        JMenuItem m2item1 = new JMenuItem("Open new .obj file");
        m2item1.addActionListener(this);

        JMenuItem m2item2 = new JMenuItem("Open cow.obj");
        m2item2.addActionListener(this);

        JMenuItem m2item3 = new JMenuItem("Open teapot.obj");
        m2item3.addActionListener(this);

        m2.add(m2item1);
        m2.add(m2item2);
        m2.add(m2item3);

        // adding all menus to menu bar
        this.add(m1);
        this.add(m2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "Toggle debug":
                f.consoleWrite("[System] Toggled debug: " + f.toggleDebug() + "\n");
                break;
            case "Toggle auto recenter":
                f.consoleWrite("[System] Toggled auto-recenter: " + f.toggleAutoRecenter() + "\n");
                break;
            case "Toggle move relative to camera":
                f.consoleWrite("[System] Toggled move relative to cam: " + f.toggleMoveRelativeToCam() + "\n");
                break;
            case "Open new .obj file":
                f.loadObject();
                break;

            case "Open cow.obj":
                f.loadCustomObject(new File("src/resources/objs/cow-nonormals.obj"));
                break;

            case "Open teapot.obj":
                f.loadCustomObject(new File("src/resources/objs/teapot.obj"));
                break;
        }
    }
}
