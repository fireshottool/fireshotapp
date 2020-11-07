package me.fox.ui.components.draw;

import lombok.Getter;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 06.11.2020)
 */

@Getter
public class Circle implements Drawable {

    private int x, y, width, height;
    private final Color color;

    public Circle(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (this.width != 0 && this.height != 0) {
            g2d.fillOval(this.x - (this.width / 2), this.y - (this.height / 2), this.width, this.height);
        }
    }
}
