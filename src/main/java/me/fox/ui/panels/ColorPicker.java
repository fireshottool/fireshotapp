package me.fox.ui.panels;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 11.11.2020)
 */

public class ColorPicker extends JPanel {

    private final JColorChooser colorChooser = new JColorChooser();

    public ColorPicker(ChangeListener changeListener, Color initialColor) {
        this.setVisible(false);
        this.setLayout(null);
        this.setSize(400, 400);
        this.colorChooser.setColor(initialColor);

        this.colorChooser.getSelectionModel().addChangeListener(changeListener);

        this.setupPanels();
    }

    private void setupPanels() {
        Component hsvPanel = this.colorChooser.getChooserPanels()[1].getComponents()[4];
        Component sideBar = this.colorChooser.getChooserPanels()[1].getComponents()[3];

        hsvPanel.setSize(200, 200);
        hsvPanel.setLocation(0, 0);
        sideBar.setSize(30, 200);
        sideBar.setLocation(200, 0);

        this.add(hsvPanel);
        this.add(sideBar);
    }

    public Color getColor() {
        return this.colorChooser.getColor();
    }
}
