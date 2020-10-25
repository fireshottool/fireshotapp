package me.fox.ui.components.draw;

import me.fox.ui.components.ScalePoint;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

public abstract class Rectangle extends java.awt.Rectangle {

    /**
     * Check if a point is in the {@link ScalePoint} {@link java.awt.Rectangle} or not
     *
     * @param point to check if it is in the {@link ScalePoint} {@link java.awt.Rectangle} or not
     * @return whether the point is in the {@link ScalePoint} {@link java.awt.Rectangle} or not
     */
    public boolean isPointInRect(Point point) {
        return point.x > this.x && point.y > this.y && point.x < this.x + this.width && point.y < this.y + this.height;
    }

    public java.awt.Rectangle reCalcRect() {
        if (width < 0 && height < 0) {
            x += width;
            width = -width;
            y += height;
           height = -height;
        } else if (width < 0) {
            x += width;
            width = -width;
        } else if (height < 0) {
            y += height;
            height = -height;
        }
        return this;
    }
}
