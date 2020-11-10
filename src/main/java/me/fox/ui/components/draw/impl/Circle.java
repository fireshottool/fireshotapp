package me.fox.ui.components.draw.impl;

import lombok.Getter;
import me.fox.ui.components.draw.Drawable;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 06.11.2020)
 */

@Getter
public class Circle implements Drawable {

    private final Color color;
    private final int x, y;
    private final boolean fill;

    private int width;
    private int height;

    public Circle(int x, int y, Color color, boolean fill) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.fill = fill;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        if (this.width != 0 && this.height != 0) {
            if (this.fill) {
                g2d.fillOval(this.x - (this.width / 2), this.y - (this.height / 2), this.width, this.height);
                return;
            }
            g2d.drawOval(this.x - (this.width / 2), this.y - (this.height / 2), this.width, this.height);
        }
    }
}
