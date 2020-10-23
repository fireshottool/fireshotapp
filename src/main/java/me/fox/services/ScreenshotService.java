package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.ui.components.Drawable;
import me.fox.ui.components.ScalableRectangle;
import me.fox.ui.frames.ScreenshotFrame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
@Setter
public class ScreenshotService implements Drawable {

    private final ScreenshotFrame screenshotFrame;
    private final ScalableRectangle selectionRectangle;
    private final DrawService drawService;

    private BufferedImage image;

    public ScreenshotService(ScreenshotFrame screenshotFrame, DrawService drawService) {
        this.screenshotFrame = screenshotFrame;
        this.selectionRectangle = new ScalableRectangle(drawService, screenshotFrame);
        this.drawService = drawService;
        this.drawService.registerDrawable(this, 0);
    }

    public void show() {
        this.createScreenshot();
        this.screenshotFrame.setVisible(true);
    }

    public void resetAndHide() {
        this.image = null;
        this.selectionRectangle.setRect(-10, -10, 0, 0);
        this.screenshotFrame.setVisible(false);
    }

    public void createScreenshot() {
        int x = this.screenshotFrame.getX();
        int y = this.screenshotFrame.getY();
        int width = this.screenshotFrame.getWidth();
        int height = this.screenshotFrame.getHeight();
        try {
            this.image = new Robot().createScreenCapture(new Rectangle(x, y, width, height));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (this.image != null) {
            int width = this.screenshotFrame.getWidth();
            int height = this.screenshotFrame.getHeight();
            g2d.drawImage(image, 0, 0, width, height, null);

            //Draw black overlay
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, width, height);

            //Draw selected image part
            this.drawSelected(g2d, this.selectionRectangle);
        }
    }

    private BufferedImage takeScreenshot(int x, int y, int width, int height) {
        try {
            return this.image.getSubimage(x, y, width, height);
        } catch (RasterFormatException e) {
            //Selection over top left corner
            if (x < this.image.getMinX() && y < this.image.getMinY()) {
                return this.image.getSubimage(this.image.getMinX(), this.image.getMinY(), width - Math.abs(x), height - Math.abs(y));
            } //Selection over bot right corner
            else if (x + width > this.image.getMinX() + this.image.getWidth() && y + height > this.image.getMinY() + this.image.getHeight()) {
                return this.image.getSubimage(x, y, width - ((x + width) - this.image.getWidth()), height - ((y + height) - this.image.getHeight()));
            } //Selection over bot left corner
            else if (x < this.image.getMinX() && y + height > this.image.getMinY() + this.image.getHeight()) {
                return this.image.getSubimage(this.image.getMinX(), y, width - Math.abs(x), height - ((y + height) - this.image.getHeight()));
            } //Selection over top right corner
            else if (x + width > this.image.getMinX() + this.image.getWidth() && y < this.image.getMinY()) {
                return this.image.getSubimage(x, this.image.getMinY(), width - ((x + width) - this.image.getWidth()), height - Math.abs(y));
            } //Selection over left side
            else if (x < this.image.getMinX()) {
                return this.image.getSubimage(this.image.getMinX(), y, width - Math.abs(x), height);
            } //Selection over bot side
            else if (y < this.image.getMinY()) {
                return this.image.getSubimage(x, this.image.getMinY(), width, height - Math.abs(y));
            } //Selection over right side
            else if (x + width > this.image.getMinX() + this.image.getWidth()) {
                return this.image.getSubimage(x, y, width - ((x + width) - this.image.getWidth()), height);
            } //Selection over top side
            else if (y + height > this.image.getMinY() + this.image.getHeight()) {
                return this.image.getSubimage(x, y, width, height - ((y + height) - this.image.getHeight()));
            }
            return null;
        }
    }

    private void drawSelected(Graphics2D g2d, Rectangle selection) {
        if (selection.width != 0 && selection.height != 0) {
            if (selection.width < 0 && selection.height < 0) {
                BufferedImage overlayScreen = this.takeScreenshot(selection.x - Math.abs(selection.width), selection.y - Math.abs(selection.height), Math.abs(selection.width), Math.abs(selection.height));
                g2d.drawImage(overlayScreen, Math.max(selection.x - Math.abs(selection.width), 0), Math.max(selection.y - Math.abs(selection.height), 0), null);
            } else if (selection.width < 0) {
                BufferedImage overlayScreen = this.takeScreenshot(selection.x - Math.abs(selection.width), selection.y, Math.abs(selection.width), selection.height);
                g2d.drawImage(overlayScreen, Math.max(selection.x - Math.abs(selection.width), 0), Math.max(selection.y, 0), null);
            } else if (selection.height < 0) {
                BufferedImage overlayScreen = this.takeScreenshot(selection.x, selection.y - Math.abs(selection.height), selection.width, Math.abs(selection.height));
                g2d.drawImage(overlayScreen, Math.max(selection.x, 0), Math.max(selection.y - Math.abs(selection.height), 0), null);
            } else {
                BufferedImage overlayScreen = this.takeScreenshot(selection.x, selection.y, selection.width, selection.height);
                g2d.drawImage(overlayScreen, Math.max(selection.x, 0), Math.max(selection.y, 0), null);
            }
        }
    }
}
