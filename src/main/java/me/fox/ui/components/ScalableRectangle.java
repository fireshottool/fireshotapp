package me.fox.ui.components;

import lombok.Getter;
import lombok.Setter;
import me.fox.listeners.mouse.ScalableRectListener;
import me.fox.services.DrawService;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.frames.ScreenshotFrame;

import java.awt.*;
import java.util.Arrays;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
@Setter
public class ScalableRectangle extends Rectangle implements Drawable {

    private final DrawService drawService;
    private final ScreenshotFrame screenshotFrame;
    private final ScalePoint[] scalePoints = this.createPoints();
    private final ScalableRectListener listener = new ScalableRectListener(this);
    private final Stroke stroke = new BasicStroke();
    private int cursor = 1;

    /**
     * Constructor for {@link ScalableRectangle}
     *
     * @param drawService     to set {@link ScalableRectangle#drawService}
     * @param screenshotFrame to set {@link ScalableRectangle#screenshotFrame}
     */
    public ScalableRectangle(DrawService drawService, ScreenshotFrame screenshotFrame) {
        this.drawService = drawService;
        this.drawService.registerDrawable(this, 2);

        this.screenshotFrame = screenshotFrame;
        screenshotFrame.registerMouseListener(this.listener);

        this.setRect(-10, -10, 0, 0);
    }

    /**
     * Create all 8 scale points
     *
     * @return the array of scale points
     */
    private ScalePoint[] createPoints() {
        ScalePoint[] scalePoints = new ScalePoint[8];
        int dirCount = 4;
        for (int i = 0; i < scalePoints.length; i++) {
            scalePoints[i] = new ScalePoint(dirCount);
            dirCount++;
        }
        return scalePoints;
    }

    public ScalableRectangle reCalcRect() {
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

    @Override
    public void draw(Graphics2D g2d) {
        //Draw scalePoints
        g2d.setStroke(this.stroke);
        if (height == 0 && width == 0) return;
        Arrays.stream(this.scalePoints).forEach(var -> {
            var.updateLocation(this);
            g2d.setColor(Color.black);
            g2d.fillRect(var.x, var.y, var.width, var.height);
            g2d.setColor(Color.white);
            g2d.drawRect(var.x, var.y, var.width, var.height);
        });
        //Draw self
        Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);

        g2d.setStroke(dashed);

        g2d.drawLine(this.x, this.y, this.x + this.width - 1, this.y);
        g2d.drawLine(this.x + this.width, this.y, this.x + this.width, this.y + this.height - 1);
        g2d.drawLine(this.x + this.width, this.y + this.height, this.x + 1, this.y + this.height);
        g2d.drawLine(this.x, this.y + this.height, this.x, this.y + 1);
    }
}
