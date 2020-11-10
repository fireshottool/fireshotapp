package me.fox.ui.components.draw;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

public interface Drawable {

    /**
     * Draw with this method
     *
     * @param g2d {@link Graphics2D} to draw
     */
    void draw(Graphics2D g2d);
}
