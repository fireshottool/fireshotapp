package me.fox.listeners.mouse;

import lombok.Getter;
import me.fox.Fireshotapp;
import me.fox.ui.components.ScalableRectangle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
public class ScalableRectListener extends MouseAdapter {

    private final ScalableRectangle scalableRectangle;

    private Integer distanceX, distanceY;
    private boolean drag, diagonal;

    /**
     * Constructor for {@link ScalableRectListener}
     *
     * @param scalableRectangle to set {@link ScalableRectListener#scalableRectangle}
     */
    public ScalableRectListener(ScalableRectangle scalableRectangle) {
        this.scalableRectangle = scalableRectangle;
    }

    /**
     * Updates the {@link ScalableRectangle#getCursor()}.
     *
     * @param event to update the cursor
     */
    private void updateCursor(MouseEvent event) {
        this.scalableRectangle.setCursor(Cursor.CROSSHAIR_CURSOR);
        if (this.scalableRectangle.contains(event.getPoint())) {
            this.scalableRectangle.setCursor(Cursor.MOVE_CURSOR);
        }
        Arrays.stream(this.scalableRectangle.getScalePoints()).filter(var ->
                var.contains(event.getPoint())).forEach(var -> this.scalableRectangle.setCursor(var.getDirection()));
        this.scalableRectangle.getScreenshotFrame().updateCursor(this.scalableRectangle.getCursor());
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (this.scalableRectangle.getDrawService().isDraw()) return;
        Fireshotapp.getInstance().getScreenService().getScreenshotToolbox().hideSelf();

        if (this.scalableRectangle.contains(event.getPoint()) &&
                this.scalableRectangle.getCursor() == Cursor.MOVE_CURSOR) {
            drag = true;
            distanceX = event.getX() - this.scalableRectangle.x;
            distanceY = event.getY() - this.scalableRectangle.y;
            return;
        }
        int x = this.scalableRectangle.x;
        int y = this.scalableRectangle.y;
        int height = this.scalableRectangle.height;
        int width = this.scalableRectangle.width;

        switch (this.scalableRectangle.getCursor()) {
            case Cursor.SW_RESIZE_CURSOR:
                this.scalableRectangle.setRect(x + width, y, -width, height);
                diagonal = true;
                break;
            case Cursor.SE_RESIZE_CURSOR:
                diagonal = true;
                break;
            case Cursor.NW_RESIZE_CURSOR:
                this.scalableRectangle.setRect(x + width, y + height, -width, -height);
                diagonal = true;
                break;
            case Cursor.NE_RESIZE_CURSOR:
                this.scalableRectangle.setRect(x, y + height, width, -height);
                diagonal = true;
                break;
            case Cursor.N_RESIZE_CURSOR:
                this.scalableRectangle.setRect(x, y + height, width, -height);
                diagonal = false;
                break;
            case Cursor.W_RESIZE_CURSOR:
                this.scalableRectangle.setRect(x + width, y, -width, height);
                diagonal = false;
                break;
            case Cursor.E_RESIZE_CURSOR:
            case Cursor.S_RESIZE_CURSOR:
                diagonal = false;
                break;
            case Cursor.CROSSHAIR_CURSOR:
                this.scalableRectangle.setRect(event.getX(), event.getY(), 0, 0);
                diagonal = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (this.scalableRectangle.getDrawService().isDraw()) return;

        if (drag) {
            drag = false;
        } else {
            int x = this.scalableRectangle.x;
            int y = this.scalableRectangle.y;
            int height = this.scalableRectangle.height;
            int width = this.scalableRectangle.width;

            switch (this.scalableRectangle.getCursor()) {
                case Cursor.W_RESIZE_CURSOR:
                case Cursor.E_RESIZE_CURSOR:
                    if (width < 0) {
                        this.scalableRectangle.setRect(x + width, y, -width, height);
                    }
                    break;
                case Cursor.N_RESIZE_CURSOR:
                case Cursor.S_RESIZE_CURSOR:
                    if (height < 0) {
                        this.scalableRectangle.setRect(x, y + height, width, -height);
                    }
            }
            this.scalableRectangle.setRect(this.scalableRectangle.reCalcRect());
        }
        Fireshotapp.getInstance().getScreenService().getScreenshotToolbox().showSelf();
    }

    @Override
    public void mouseDragged(MouseEvent event) {
        if (this.scalableRectangle.getDrawService().isDraw()) return;

        if (drag) {
            this.scalableRectangle.x = event.getX() - distanceX;
            this.scalableRectangle.y = event.getY() - distanceY;
            return;
        }

        int x = this.scalableRectangle.x;
        int y = this.scalableRectangle.y;
        int height = this.scalableRectangle.height;
        int width = this.scalableRectangle.width;

        if (diagonal) {
            this.scalableRectangle.setRect(x, y, event.getX() - x, event.getY() - y);
        } else {
            switch (this.scalableRectangle.getCursor()) {
                case Cursor.W_RESIZE_CURSOR:
                case Cursor.E_RESIZE_CURSOR:
                    this.scalableRectangle.setRect(x, y, event.getX() - x, height);
                    break;
                case Cursor.N_RESIZE_CURSOR:
                case Cursor.S_RESIZE_CURSOR:
                    this.scalableRectangle.setRect(x, y, width, event.getY() - y);
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        if (this.scalableRectangle.getDrawService().isDraw()) return;
        this.updateCursor(event);
    }
}
