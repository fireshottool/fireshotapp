package me.fox.listeners.mouse;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.adapter.MouseListenerAdapter;
import me.fox.ui.components.ScalableRectangle;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
public class ScalableRectListener extends MouseListenerAdapter {

    private final ScalableRectangle parent;

    private Integer distanceX, distanceY;
    private boolean drag, diagonal;

    /**
     * Constructor for {@link ScalableRectListener}
     *
     * @param parent to set {@link ScalableRectListener#parent}
     */
    public ScalableRectListener(ScalableRectangle parent) {
        this.parent = parent;
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (this.parent.getDrawService().isDraw()) return;
        Fireshot.getInstance().getScreenService().getScreenshotToolbox().hideSelf();

        if (this.parent.isPointInRect(event.getPoint()) && this.parent.getCursor() == Cursor.MOVE_CURSOR) {
            drag = true;
            distanceX = event.getX() - this.parent.x;
            distanceY = event.getY() - this.parent.y;
            return;
        }
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
                this.parent.setRect(x + width, y + height, -width, -height);
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
        if (this.parent.getDrawService().isDraw()) return;

        if (drag) {
            drag = false;
        } else {
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
        Fireshot.getInstance().getScreenService().getScreenshotToolbox().showSelf();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (this.parent.getDrawService().isDraw()) return;

        if (drag) {
            this.parent.x = event.getX() - distanceX;
            this.parent.y = event.getY() - distanceY;
            return;
        }

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
        if (this.parent.getDrawService().isDraw()) return;
        this.updateCursor(event);
    }

    /**
     * Update the {@link ScalableRectangle#getCursor()}
     *
     * @param event to update the cursor
     */
    private void updateCursor(MouseEvent event) {
        this.parent.setCursor(Cursor.CROSSHAIR_CURSOR);
        if (this.parent.isPointInRect(event.getPoint())) {
            this.parent.setCursor(Cursor.MOVE_CURSOR);
        }
        Arrays.stream(this.parent.getScalePoints()).filter(var -> var.isPointInRect(event.getPoint())).forEach(var -> this.parent.setCursor(var.getDirection()));
        this.parent.getScreenshotFrame().updateCursor(this.parent.getCursor());
    }
}
