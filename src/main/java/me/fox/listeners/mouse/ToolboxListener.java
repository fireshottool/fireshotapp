package me.fox.listeners.mouse;

import me.fox.adapter.MouseListenerAdapter;
import me.fox.ui.panels.Toolbox;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

public class ToolboxListener extends MouseListenerAdapter {

    private final Toolbox parent;
    private int distanceX, distanceY;

    public ToolboxListener(Toolbox parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        distanceX = event.getXOnScreen() - this.parent.getX();
        distanceY = event.getYOnScreen() - this.parent.getY();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        this.parent.setLocation(event.getXOnScreen() - distanceX, event.getYOnScreen() - distanceY);
    }

    @Override
    public void mouseReleased(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {
        this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
