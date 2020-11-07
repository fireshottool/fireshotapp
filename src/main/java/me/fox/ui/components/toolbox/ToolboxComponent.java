package me.fox.ui.components.toolbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

public abstract class ToolboxComponent extends JButton {

    public ToolboxComponent(BufferedImage icon) {
        this.setLayout(null);
        this.setOpaque(true);
        this.setBackground(Color.decode("#ebebeb"));
        if (icon != null) {
            this.setIcon(new ImageIcon(icon.getScaledInstance(26, 26, Image.SCALE_SMOOTH)));
        }
    }

    public void select(ActionEvent event) {
        if (this.isBorderPainted()) {
            this.setBorderPainted(false);
            this.setBackground(Color.decode("#9c9c9c"));
        } else {
            this.setBorderPainted(true);
            this.setBackground(Color.decode("#ebebeb"));
        }
    }
}
