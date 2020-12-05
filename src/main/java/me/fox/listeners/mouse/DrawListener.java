package me.fox.listeners.mouse;

import me.fox.Fireshot;
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
            if (Fireshot.getInstance().getHotkeyService().getPressedKeys().contains(16)) {
                this.drawService.addPoint(event.getPoint());
                return;
            }
            if (this.drawService.isLine()) {
                if (!second) {
                    this.drawService.addLine();
                    this.drawService.addPoint(event.getPoint());
                    second = true;
                    return;
                }
                this.drawService.addPoint(event.getPoint());
                second = false;
                return;
            }
            this.drawService.addLine();
            this.drawService.addPoint(event.getPoint());
        }
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (drawService.isDraw()) {
            if (this.drawService.isCircle()) {
                this.drawService.resizeCurrentCircle(event.getPoint());
                return;
            }
            if (this.drawService.isRectangle()) {
                this.drawService.resizeRectangle(event.getPoint());
                return;
            }
            if (drawService.isLine() || Fireshot.getInstance().getHotkeyService().getPressedKeys().contains(16)) {
                Line line = (Line) this.drawService.getDrawings().get(this.drawService.getCurrentIndex());
                if (line.getPoints().size() == 1) {
                    line.getPoints().add(event.getPoint());
                    second = false;
                } else {
                    line.getPoints().set(line.getPoints().size() - 1, event.getPoint());
                }
                return;
            }

            this.drawService.addPoint(event.getPoint());
        }
    }
}
