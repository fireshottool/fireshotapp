package me.fox.listeners.mouse;

import me.fox.Fireshotapp;
import me.fox.services.DrawService;
import me.fox.ui.components.draw.impl.Line;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

public class DrawListener extends MouseAdapter {

    private final DrawService drawService;
    private boolean second;

    /**
     * Constructor for {@link DrawListener}
     *
     * @param drawService to set {@link DrawListener#drawService}
     */
    public DrawListener(DrawService drawService) {
        this.drawService = drawService;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (this.drawService.isDraw()) {
            if (this.drawService.isCircle()) {
                this.drawService.addCircle(event.getPoint());
                return;
            }
            if (this.drawService.isRectangle()) {
                this.drawService.addRectangle(event.getPoint());
                return;
            }
            if (Fireshotapp.getInstance().getHotkeyService().getPressedKeys().contains(16)) {
                this.drawService.addPoint(event.getPoint());
                return;
            }
            if (this.drawService.isLine()) {
                if (!this.second) {
                    this.drawService.addLine();
                    this.drawService.addPoint(event.getPoint());
                    this.second = true;
                    return;
                }
                this.drawService.addPoint(event.getPoint());
                this.second = false;
                return;
            }
            this.drawService.addLine();
            this.drawService.addPoint(event.getPoint());
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (this.drawService.isDraw()) {
            if (this.drawService.isCircle()) {
                this.drawService.resizeCurrentCircle(event.getPoint());
                return;
            }
            if (this.drawService.isRectangle()) {
                this.drawService.resizeRectangle(event.getPoint());
                return;
            }
            if (this.drawService.isLine() || Fireshotapp.getInstance().getHotkeyService().getPressedKeys().contains(16)) {
                Line line = (Line) this.drawService.getDrawings().peek();
                if (line.getPoints().size() == 1) {
                    line.getPoints().add(event.getPoint());
                    this.second = false;
                } else {
                    line.getPoints().set(line.getPoints().size() - 1, event.getPoint());
                }
                return;
            }

            this.drawService.addPoint(event.getPoint());
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        if (this.drawService.isDraw() &&
                !Fireshotapp.getInstance().getScreenshotFrame().getCursor().getName().equals("drawing")) {
            Fireshotapp.getInstance().getScreenshotFrame().setCursor(this.drawService.getDrawCursor());
            Fireshotapp.getInstance().getScreenshotFrame().setCursor(this.drawService.getDrawCursor());
        }
    }
}
