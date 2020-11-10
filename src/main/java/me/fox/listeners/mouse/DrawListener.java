package me.fox.listeners.mouse;

import me.fox.Fireshot;
import me.fox.adapter.MouseListenerAdapter;
import me.fox.services.DrawService;
import me.fox.ui.components.draw.impl.Line;

import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

public class DrawListener extends MouseListenerAdapter {

    private final DrawService parent;
    private boolean second;

    public DrawListener(DrawService parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (this.parent.isDraw()) {
            if (Fireshot.getInstance().getHotkeyService().getPressedKeys().contains(16)) {
                this.parent.addPoint(event.getPoint());
                return;
            }
            if (this.parent.isCircle()) {
                this.parent.addCircle(event.getPoint());
                return;
            }
            if (this.parent.isRectangle()) {
                this.parent.addRectangle(event.getPoint());
                return;
            }
            if (this.parent.isLine()) {
                if (!second) {
                    this.parent.addLine();
                    this.parent.addPoint(event.getPoint());
                    second = true;
                    return;
                }
                this.parent.addPoint(event.getPoint());
                second = false;
                return;
            }
            this.parent.addLine();
            this.parent.addPoint(event.getPoint());
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (parent.isDraw()) {
            if (this.parent.isCircle()) {
                this.parent.resizeCurrentCircle(event.getPoint());
                return;
            }
            if (this.parent.isRectangle()) {
                this.parent.resizeRectangle(event.getPoint());
                return;
            }
            if (parent.isLine() || Fireshot.getInstance().getHotkeyService().getPressedKeys().contains(16)) {
                Line line = (Line) this.parent.getDrawings().get(this.parent.getCurrentIndex());
                if (line.getPoints().size() == 1) {
                    line.getPoints().add(event.getPoint());
                    second = false;
                } else {
                    line.getPoints().set(line.getPoints().size() - 1, event.getPoint());
                }
                return;
            }

            this.parent.addPoint(event.getPoint());
        }
    }
}
