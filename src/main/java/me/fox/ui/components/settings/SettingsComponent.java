package me.fox.ui.components.settings;

import javax.swing.*;
import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 19.11.2020)
 */


public abstract class SettingsComponent extends JPanel {

    public SettingsComponent(Point location) {
        this.setSize(400, 80);
        this.setLocation(location);
        this.setVisible(true);
        this.setLayout(null);
    }
}
