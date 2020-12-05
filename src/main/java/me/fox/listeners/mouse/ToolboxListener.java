package me.fox.listeners.mouse;

import me.fox.ui.panels.toolbox.Toolbox;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

public class ToolboxListener extends MouseAdapter {

    private final Toolbox toolbox;
    private int distanceX, distanceY;

    public ToolboxListener(Toolbox toolbox) {
        this.toolbox = toolbox;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        distanceX = event.getXOnScreen() - this.toolbox.getX();
        distanceY = event.getYOnScreen() - this.toolbox.getY();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        this.toolbox.setLocation(event.getXOnScreen() - distanceX, event.getYOnScreen() - distanceY);
    }

    @Override
    public void mouseReleased(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {
        this.toolbox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
