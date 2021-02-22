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

    /**
     * Constructor for {@link ToolboxListener}
     *
     * @param toolbox to set {@link ToolboxListener#toolbox}
     */
    public ToolboxListener(Toolbox toolbox) {
        this.toolbox = toolbox;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        this.distanceX = event.getXOnScreen() - this.toolbox.getX();
        this.distanceY = event.getYOnScreen() - this.toolbox.getY();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        this.toolbox.setLocation(event.getXOnScreen() - this.distanceX, event.getYOnScreen() - this.distanceY);
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        this.toolbox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
