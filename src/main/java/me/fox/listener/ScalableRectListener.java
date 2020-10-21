package me.fox.listener;

import me.fox.adapter.MouseListenerAdapter;
import me.fox.ui.components.ScalableRectangle;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

public class ScalableRectListener extends MouseListenerAdapter {

    private final ScalableRectangle parent;

    private Integer distanceX, distanceY;
    private boolean drag, diagonal;

    public ScalableRectListener(ScalableRectangle parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        int x = this.parent.x;
        int y = this.parent.y;
        int height = this.parent.height;
        int width = this.parent.width;

        switch (this.parent.getCursor()) {
            case Cursor.SW_RESIZE_CURSOR:
                this.parent.setRect(x + width, y, -width, height);
                diagonal = true;
                break;
            case Cursor.SE_RESIZE_CURSOR:
                diagonal = true;
                break;
            case Cursor.NW_RESIZE_CURSOR:
                this.parent.setRect(x + width, y + height, width, -height);
                diagonal = true;
                break;
            case Cursor.NE_RESIZE_CURSOR:
                this.parent.setRect(x, y + height, width, -height);
                diagonal = true;
                break;
            case Cursor.N_RESIZE_CURSOR:
                this.parent.setRect(x, y + height, width, -height);
                diagonal = false;
                break;
            case Cursor.W_RESIZE_CURSOR:
                this.parent.setRect(x + width, y, -width, height);
                diagonal = false;
                break;
            case Cursor.E_RESIZE_CURSOR:
            case Cursor.S_RESIZE_CURSOR:
                diagonal = false;
                break;
            case Cursor.CROSSHAIR_CURSOR:
                this.parent.setRect(event.getX(), event.getY(), 0, 0);
                diagonal = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        int x = this.parent.x;
        int y = this.parent.y;
        int height = this.parent.height;
        int width = this.parent.width;

        switch (this.parent.getCursor()) {
            case Cursor.W_RESIZE_CURSOR:
            case Cursor.E_RESIZE_CURSOR:
                if (width < 0) {
                    this.parent.setRect(x + width, y, -width, height);
                }
                break;
            case Cursor.N_RESIZE_CURSOR:
            case Cursor.S_RESIZE_CURSOR:
                if (height < 0) {
                    this.parent.setRect(x, y + height, width, -height);
                }
        }
        this.parent.setRect(this.parent.reCalcRect());
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        int x = this.parent.x;
        int y = this.parent.y;
        int height = this.parent.height;
        int width = this.parent.width;

        if (diagonal) {
            this.parent.setRect(x, y, event.getX() - x, event.getY() - y);
        } else {
            switch (this.parent.getCursor()) {
                case Cursor.W_RESIZE_CURSOR:
                case Cursor.E_RESIZE_CURSOR:
                    this.parent.setRect(x, y, event.getX() - x, height);
                    break;
                case Cursor.N_RESIZE_CURSOR:
                case Cursor.S_RESIZE_CURSOR:
                    this.parent.setRect(x, y, width, event.getY() - y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        this.updateCursor(event);
    }

    private void updateCursor(MouseEvent event) {
        if (this.parent.isPointInRect(event.getPoint())) {
            this.parent.setCursor(Cursor.MOVE_CURSOR);
        }
        Arrays.stream(this.parent.getScalePoints()).filter(var -> var.isPointInRect(event.getPoint())).forEach(var -> {
            this.parent.setCursor(var.getDirection());
        });
    }
}
