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

        if (this.fill) {
            this.drawFillRect(g2d);
            return;
        }
        this.drawRect(g2d);
    }

    private void drawRect(Graphics2D g2d) {
        int x = this.x;
        int y = this.y;
        int width = Math.abs(this.width);
        int height = Math.abs(this.height);
        if (width != 0 && height != 0) {
            if (this.width < 0 && this.height < 0) {
                g2d.drawRect(x - width, y - height, width, height);
            } else if (this.width < 0) {
                g2d.drawRect(x - width, y, width, height);
            } else if (this.height < 0) {
                g2d.drawRect(x, y - height, width, height);
            } else {
                g2d.drawRect(x, y, width, height);
            }
        }
    }

    private void drawFillRect(Graphics2D g2d) {
        int x = this.x;
        int y = this.y;
        int width = Math.abs(this.width);
        int height = Math.abs(this.height);
        if (width != 0 && height != 0) {
            if (this.width < 0 && this.height < 0) {
                g2d.fillRect(x - width, y - height, width, height);
            } else if (this.width < 0) {
                g2d.fillRect(x - width, y, width, height);
            } else if (this.height < 0) {
                g2d.fillRect(x, y - height, width, height);
            } else {
                g2d.fillRect(x, y, width, height);
            }
        }
    }
}
