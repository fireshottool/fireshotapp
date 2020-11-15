package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshot;
import me.fox.components.ClipboardImage;
import me.fox.config.ScreenshotConfig;
import me.fox.ui.components.ScalableRectangle;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.frames.ScreenshotFrame;
import me.fox.utils.Util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.IOException;

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
    private Color dimColor;
    private boolean localSave, upload;

    public ScreenshotService(ScreenshotFrame screenshotFrame, DrawService drawService) {
        this.drawService = drawService;
        this.screenshotFrame = screenshotFrame;
        this.drawService.registerDrawable(this, 0);
        this.selectionRectangle = new ScalableRectangle(drawService, screenshotFrame);
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

    public void confirmScreenShot() throws IOException {
        int x = this.selectionRectangle.x;
        int y = this.selectionRectangle.y;
        int width = this.selectionRectangle.width;
        int height = this.selectionRectangle.height;
        BufferedImage screenshot = this.takeScreenshot(x, y, width, height);
        if (screenshot == null) return;

        this.drawService.draw((Graphics2D) screenshot.getGraphics());

        if (this.isUpload()) {
            this.uploadImage(screenshot);
        }

        if (this.isLocalSave()) {
            this.saveImage(screenshot);
        }
    }

    public void applyConfig(ScreenshotConfig screenshotConfig) {
        this.dimColor = Color.decode(screenshotConfig.getDimColor());
        this.localSave = screenshotConfig.isLocalSave();
        this.upload = screenshotConfig.isUpload();
    }

    private void saveImage(BufferedImage screenshot) throws IOException {
        String fileName = Util.generateRandomString(18);

        Fireshot.getInstance().getFileService().saveImage(screenshot, fileName);

        ClipboardImage.write(screenshot);
    }

    private void uploadImage(BufferedImage screenshot) {
        //TODO Implement RequestService.java
    }

    private BufferedImage takeScreenshot(int x, int y, int width, int height) {
        if (width == 0 || height == 0 || image == null) return null;
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
        int x = selection.x;
        int y = selection.y;
        int width = Math.abs(selection.width);
        int height = Math.abs(selection.height);
        if (width != 0 && height != 0) {
            if (selection.width < 0 && selection.height < 0) {
                BufferedImage overlayScreen = this.takeScreenshot(x - width, y - height, width, height);
                g2d.drawImage(overlayScreen, Math.max(x - width, 0), Math.max(y - height, 0), null);
            } else if (selection.width < 0) {
                BufferedImage overlayScreen = this.takeScreenshot(x - width, y, width, height);
                g2d.drawImage(overlayScreen, Math.max(x - width, 0), Math.max(y, 0), null);
            } else if (selection.height < 0) {
                BufferedImage overlayScreen = this.takeScreenshot(x, y - height, width, height);
                g2d.drawImage(overlayScreen, Math.max(x, 0), Math.max(y - height, 0), null);
            } else {
                BufferedImage overlayScreen = this.takeScreenshot(x, y, width, height);
                g2d.drawImage(overlayScreen, Math.max(x, 0), Math.max(y, 0), null);
            }
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
}
