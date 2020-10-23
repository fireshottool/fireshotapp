package me.fox.listeners.mouse;

import me.fox.adapter.MouseListenerAdapter;
import me.fox.services.DrawService;

import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

public class DrawListener extends MouseListenerAdapter {

    private final DrawService parent;

    public DrawListener(DrawService parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (parent.isDraw()) {
            this.parent.addLine();
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (parent.isDraw()) {
            this.parent.addPoint(event.getPoint());
        }
    }
}
