package me.fox.ui.components.toolbox;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

public abstract class ToolboxComponent extends JButton {

    public ToolboxComponent(BufferedImage icon) {
        this.setBackground(Color.decode("#ebebeb"));
        this.setLayout(null);
        if (icon != null) {
            this.setIcon(new ImageIcon(icon.getScaledInstance(26, 26, Image.SCALE_SMOOTH)));
        }
    }
}
