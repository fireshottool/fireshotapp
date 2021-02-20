package me.fox.ui.components.draw.impl;

import lombok.Getter;
import lombok.Setter;
import me.fox.ui.components.draw.Drawable;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 08.11.2020)
 */

@Getter
@Setter
public class Rectangle extends java.awt.Rectangle implements Drawable {

    private final Color color;
    private final Stroke stroke;
    private final boolean fill;

    /**
     * Constructor for {@link Rectangle}
     *
     * @param x           to initialize {@link Rectangle#x}
     * @param y           to initialize {@link Rectangle#y}
     * @param color       to initialize {@link Rectangle#color}
     * @param strokeWidth to initialize {@link Rectangle#stroke}
     * @param fill        to initialize {@link Rectangle#fill}
     */
    public Rectangle(int x, int y, Color color, float strokeWidth, boolean fill) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.fill = fill;
        this.stroke = new BasicStroke(strokeWidth);
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setStroke(this.stroke);
        int x = this.x;
        int y = this.y;
        int width = Math.abs(this.width);
        int height = Math.abs(this.height);

        if (width == 0 || height == 0)
            return;
        if (this.width < 0) {
            x -= width;
        }
        if (this.height < 0) {
            y -= height;
        }

        if (this.fill) {
            g2d.fillRect(x, y, width, height);
            return;
        }
        g2d.drawRect(x, y, width, height);
    }
}
