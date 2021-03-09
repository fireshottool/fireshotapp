package me.fox.ui.components.draw.impl;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.constants.ColorPalette;
import me.fox.constants.Strokes;
import me.fox.services.DrawService;
import me.fox.services.ScreenshotService;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.frames.ScreenshotFrame;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * @author (Ausgefuchster)
 * @version (~ 25.02.2021)
 */

@Getter
@Setter
public class ImageZoom implements Drawable {

    private int pixelCount = 11;
    private int pixelSize = 20;
    private int size = pixelCount * pixelSize;

    private Color gridColor, middleRectColor, borderColor, crossColor;

    private boolean grid, middleRect, cross;

    private void drawZoom(Graphics2D g2d, Point point) {
        ScreenshotFrame screenshotFrame = Fireshotapp.getInstance().getScreenshotFrame();

        java.awt.Rectangle rectangle = new java.awt.Rectangle(
                point.x - (this.size / (2 * this.pixelSize)),
                point.y - (this.size / (2 * this.pixelSize)),
                (this.size / this.pixelSize), (this.size / this.pixelSize)
        );

        if (point.x + this.size + this.pixelSize * 2 > screenshotFrame.getWidth())
            point.x -= this.size;

        if (point.y + this.size + this.pixelSize * 2 > screenshotFrame.getHeight())
            point.y -= this.size;

        BufferedImage image = Fireshotapp.getInstance().use(ScreenshotService.class).takeScreenshot(
                rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height
        );
        Shape clip = new Ellipse2D.Double(
                point.x,
                point.y,
                this.size, this.size
        );
        g2d.setClip(clip);
        g2d.drawImage(image, point.x, point.y, size, size, null);

        if (this.grid)
            this.drawGrid(g2d, point);
        g2d.setClip(null);
        g2d.setStroke(Strokes.WIDTH_TWO_STROKE);
        g2d.setColor(this.borderColor);
        g2d.draw(clip);
    }

    private void drawGrid(Graphics2D g2d, Point point) {
        g2d.setColor(this.gridColor);

        for (int i = 0; i < this.size; i += this.pixelSize * 2) {
            g2d.drawRect(point.x + i, point.y, this.pixelSize, this.size);
            g2d.drawRect(point.x, point.y + i, this.size, this.pixelSize);
        }

        if (!this.middleRect) return;
        int middle = (this.size / 2) - (this.pixelSize / 2);
        g2d.setColor(this.middleRectColor);
        g2d.drawRect(point.x + middle, point.y + middle, this.pixelSize, this.pixelSize);

        if (this.cross)
            this.drawCross(g2d, point, middle);
    }

    private void drawCross(Graphics2D g2d, Point point, int middle) {
        g2d.setColor(this.crossColor);

        g2d.fillRect(point.x + middle, point.y, this.pixelSize, middle);
        g2d.fillRect(point.x + middle, point.y + middle + this.pixelSize, this.pixelSize, middle);

        g2d.fillRect(point.x, point.y + middle, middle, this.pixelSize);
        g2d.fillRect(point.x + middle + this.pixelSize, point.y + middle, middle, this.pixelSize);
    }

    private void drawCoordinates(Graphics2D g2d, Point point) {
        String cords = String.format("x:%s y:%s", point.x, point.y);
        g2d.setStroke(Strokes.WIDTH_ONE_STROKE);
        g2d.setColor(ColorPalette.DARK_BLUE_170);
        int x, y;
        int width = cords.length() * 8;

        if (Fireshotapp.getInstance().use(ScreenshotService.class).isZoom() &&
                !Fireshotapp.getInstance().use(DrawService.class).isDraw()) {
            x = point.x + (this.size / 2) - (width / 2);
            y = point.y + this.size + this.pixelSize;
        } else {
            x = point.x + this.pixelSize;
            y = point.y + this.pixelSize;
        }

        g2d.fillRoundRect(x, y, width, 20, 10, 10);
        g2d.setColor(ColorPalette.DARK_BLUE_LIGHTER_170);
        g2d.drawRoundRect(x, y, width, 20, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawString(cords, x + 10, y + 15);
    }

    @Override
    public void draw(Graphics2D g2d) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        g2d.setStroke(Strokes.WIDTH_ONE_STROKE);
        if (Fireshotapp.getInstance().use(ScreenshotService.class).isZoom() &&
                !Fireshotapp.getInstance().use(DrawService.class).isDraw())
            this.drawZoom(g2d, point);

        this.drawCoordinates(g2d, point);
    }
}
