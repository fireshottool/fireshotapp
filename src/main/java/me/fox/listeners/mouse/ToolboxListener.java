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

    public ToolboxListener(Toolbox parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent event) {

    }

    @Override
    public void mouseDragged(MouseEvent event) {
    }

    @Override
    public void mouseReleased(MouseEvent event) {

    }

    @Override
    public void mouseEntered(MouseEvent event) {
        this.parent.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
