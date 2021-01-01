package me.fox.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshot;
import me.fox.components.ClipboardImage;
import me.fox.components.ConfigManager;
import me.fox.config.Config;
import me.fox.config.ScreenshotConfig;
import me.fox.ui.components.ScalableRectangle;
import me.fox.ui.components.TrayIcon;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.frames.ScreenshotFrame;
import me.fox.utils.Util;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
@Setter
public class ScreenshotService implements Drawable, ConfigManager {

    private final ScreenshotFrame screenshotFrame;
    private final ScalableRectangle selectionRectangle;
    private final DrawService drawService;
    private final RequestService requestService;

    private final Stroke widthTwoStroke = new BasicStroke(2);
    private final Stroke widthOneStroke = new BasicStroke(1);

    private BufferedImage image;
    private Color dimColor;
    private Color zoomCrossColor;
    private Color zoomRasterColor;
    private boolean localSave, upload, zoom;

    public ScreenshotService(ScreenshotFrame screenshotFrame, DrawService drawService, RequestService requestService) {
        this.drawService = drawService;
        this.screenshotFrame = screenshotFrame;
        this.drawService.registerDrawable(this, 0);
        this.selectionRectangle = new ScalableRectangle(drawService, screenshotFrame);
        this.requestService = requestService;
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

    public void confirmScreenshot(boolean imageDetection, boolean googleSearch) throws IOException {
        int x = this.selectionRectangle.x;
        int y = this.selectionRectangle.y;
        int width = this.selectionRectangle.width;
        int height = this.selectionRectangle.height;
        BufferedImage screenshot = this.takeScreenshot(x, y, width, height);
        if (screenshot == null) return;

        this.drawService.draw((Graphics2D) screenshot.getGraphics());
        if (this.isUpload() || imageDetection) {
            System.out.println("upload");
            this.uploadImage(screenshot, imageDetection, googleSearch);

            if (imageDetection) return;
        }

        if (this.isLocalSave()) {
            System.out.println("local save");
            this.saveImage(screenshot);
        }
    }

    private void saveImage(BufferedImage screenshot) throws IOException {
        String fileName = Util.generateRandomString(18);

        Fireshot.getInstance().getFileService().saveImage(screenshot, fileName);

        ClipboardImage.write(screenshot);
    }

    private void uploadImage(BufferedImage screenshot, boolean imageDetection, boolean googleSearch) {
        //TODO Implement RequestService.java
        File file = new File("image.png");
        try {
            ImageIO.write(screenshot, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Futures.addCallback(this.requestService.uploadImage(new File(file.toString()), imageDetection, googleSearch),
                new FutureCallback<File>() {
                    @Override
                    public void onSuccess(@Nullable File file) {
                        if (file != null) {
                            System.out.println("Testtt");
                            file.delete();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                }, Fireshot.getInstance().getExecutorService());
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

    private void drawZoom(Graphics2D g2d) {
        ScreenshotService screenshotService = Fireshot.getInstance().getScreenshotService();
        Point pointerLocation = MouseInfo.getPointerInfo().getLocation();

        int screenWidth = screenshotFrame.getWidth();
        int screenHeight = screenshotFrame.getHeight();


        if (pointerLocation.x + 160 > screenWidth) {
            pointerLocation.x -= 140;
        }
        if (pointerLocation.y + 160 > screenHeight) {
            pointerLocation.y -= 140;
        }


        Shape shape = new Ellipse2D.Float(pointerLocation.x, pointerLocation.y, 140, 140);
        g2d.setClip(shape);

        Image subImage = screenshotService.takeScreenshot(pointerLocation.x - 6,
                pointerLocation.y - 6,
                11, 11);
        if (subImage == null) return;
        g2d.drawImage(subImage, pointerLocation.x, pointerLocation.y, 140,
                140, null);

        this.drawZoomRaster(g2d, pointerLocation);
        g2d.setStroke(this.widthTwoStroke);
        g2d.setColor(Color.white);
        g2d.draw(shape);
    }

    private void drawZoomRaster(Graphics2D g2d, Point pointerLocation) {
        //g2d.setColor(new Color(35, 255, 255, 77));
        g2d.setColor(this.zoomCrossColor);
        g2d.fillRect(pointerLocation.x + 64, pointerLocation.y, 12, 65);
        g2d.fillRect(pointerLocation.x + 64, pointerLocation.y + 74, 12, 65);
        g2d.fillRect(pointerLocation.x, pointerLocation.y + 64, 65, 12);
        g2d.fillRect(pointerLocation.x + 74, pointerLocation.y + 64, 65, 12);

        //g2d.setColor(new Color(0, 0, 0, 100));
        g2d.setColor(this.zoomRasterColor);

        int count = 0;
        for (int i = 12; i < 70; i += 12) {
            g2d.drawLine(pointerLocation.x, pointerLocation.y + i + count, pointerLocation.x + 140, pointerLocation.y + i + count);
            g2d.drawLine(pointerLocation.x + i + count, pointerLocation.y, pointerLocation.x + i + count, pointerLocation.y + 140);
            count++;
        }
        count = 0;
        for (int i = 76; i < 140; i += 12) {
            g2d.drawLine(pointerLocation.x, pointerLocation.y + i + count, pointerLocation.x + 140, pointerLocation.y + i + count);
            g2d.drawLine(pointerLocation.x + i + count, pointerLocation.y, pointerLocation.x + i + count, pointerLocation.y + 140);
            if (count == 3) continue;
            count++;
        }
        g2d.setColor(Color.white);
        g2d.setStroke(this.widthOneStroke);
        g2d.drawRect(pointerLocation.x + 64, pointerLocation.y + 64, 11, 11);
        g2d.setClip(null);
    }

    @Override
    public void applyConfig(Config config) {
        ScreenshotConfig screenshotConfig = config.getScreenshotConfig();
        Color color = Color.decode(screenshotConfig.getDimColor());
        this.dimColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 200);
        color = Color.decode(screenshotConfig.getZoomCrossColor());
        this.zoomCrossColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 77);
        color = Color.decode(screenshotConfig.getZoomRasterColor());
        this.zoomRasterColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
        this.localSave = screenshotConfig.isLocalSave();
        this.upload = screenshotConfig.isUpload();
        this.zoom = screenshotConfig.isZoom();
        TrayIcon trayIcon = Fireshot.getInstance().getSystemTray();
        trayIcon.getLocalSaveItem().setState(this.localSave);
        trayIcon.getUploadItem().setState(this.upload);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (this.image != null) {
            int width = this.screenshotFrame.getWidth();
            int height = this.screenshotFrame.getHeight();
            g2d.drawImage(image, 0, 0, width, height, null);

            //Draw black overlay
            g2d.setColor(this.dimColor);
            g2d.fillRect(0, 0, width, height);

            //Draw selected image part
            this.drawSelected(g2d, this.selectionRectangle);

            if (this.zoom && !this.drawService.isDraw()) this.drawZoom(g2d);
            if (this.zoom && !this.drawService.isDraw()) this.drawZoom(g2d);
        }
    }
}
