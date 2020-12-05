package me.fox.ui.components.settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */

public class SettingsButton extends JButton {

    public SettingsButton(String text, Point location, ActionListener actionListener) {
        super(text);

        this.setLocation(location);
        this.setSize(200, 100);
        this.setContentAreaFilled(false);
        this.setBorder(BorderFactory.createLoweredBevelBorder());
        this.setBorder(BorderFactory.createRaisedBevelBorder());
        //this.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.BLACK));
        this.addActionListener(actionListener);
    }
}
