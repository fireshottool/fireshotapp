package me.fox.ui.components.draw.impl;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.constants.ColorPalette;
import me.fox.constants.Strokes;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.frames.ScreenshotFrame;

import java.awt.Rectangle;
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

    private final int zoomFactor = 16;
    private final int size = 144;

    private Color gridColor, middleRectColor, borderColor, crossColor;

    private boolean grid, middleRect, cross;

    private void drawZoom(Graphics2D g2d, Point point) {
        ScreenshotFrame screenshotFrame = Fireshotapp.getInstance().getScreenshotFrame();
        if (point.x + size + zoomFactor > screenshotFrame.getWidth())
            point.x -= size;

        if (point.y + size + zoomFactor * 2 > screenshotFrame.getHeight())
            point.y -= size;
        Rectangle rectangle = new Rectangle(
                point.x - (size / (2 * zoomFactor)),
                point.y - (size / (2 * zoomFactor)),
                (size / zoomFactor), (size / zoomFactor)
        );
        BufferedImage image = Fireshotapp.getInstance().getScreenshotService().takeScreenshot(
                rectangle.x,
                rectangle.y,
                rectangle.width,
                rectangle.height
        );
        Shape clip = new Ellipse2D.Double(
                point.x,
                point.y,
                size, size
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
        for (int i = 0; i < size; i += zoomFactor * 2) {
            g2d.drawRect(point.x + i, point.y, zoomFactor, size);
            g2d.drawRect(point.x, point.y + i, size, zoomFactor);
        }

        if (!this.middleRect) return;
        int middle = (size / 2) - (zoomFactor / 2);
        g2d.setColor(this.middleRectColor);
        g2d.drawRect(point.x + middle, point.y + middle, zoomFactor, zoomFactor);

        if (this.cross)
            this.drawCross(g2d, point, middle);
    }

    private void drawCross(Graphics2D g2d, Point point, int middle) {
        g2d.setColor(this.crossColor);

        g2d.fillRect(point.x + middle, point.y, zoomFactor, middle);
        g2d.fillRect(point.x + middle, point.y + middle + zoomFactor, zoomFactor, middle);

        g2d.fillRect(point.x, point.y + middle, middle, zoomFactor);
        g2d.fillRect(point.x + middle + zoomFactor, point.y + middle, middle, zoomFactor);
    }

    private void drawCoordinates(Graphics2D g2d, Point point) {
        String cords = String.format("x:%s y:%s", point.x, point.y);
        g2d.setStroke(Strokes.WIDTH_ONE_STROKE);
        g2d.setColor(ColorPalette.DARK_BLUE_170);
        int x, y;
        if (Fireshotapp.getInstance().getScreenshotService().isZoom() &&
                !Fireshotapp.getInstance().getDrawService().isDraw()) {
            x = point.x + (size / 5);
            y = point.y + size + zoomFactor;
        } else {
            x = point.x + zoomFactor;
            y = point.y + zoomFactor;
        }

        g2d.fillRoundRect(x, y, cords.length() * 8, 20, 10, 10);
        g2d.setColor(ColorPalette.DARK_BLUE_LIGHTER_170);
        g2d.drawRoundRect(x, y, cords.length() * 8, 20, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawString(cords, x + 10, y + 15);
    }

    @Override
    public void draw(Graphics2D g2d) {
        Point point = MouseInfo.getPointerInfo().getLocation();
        g2d.setStroke(Strokes.WIDTH_ONE_STROKE);
        if (Fireshotapp.getInstance().getScreenshotService().isZoom() &&
                !Fireshotapp.getInstance().getDrawService().isDraw())
            this.drawZoom(g2d, point);

        this.drawCoordinates(g2d, point);
    }
}
