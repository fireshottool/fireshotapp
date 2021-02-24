package me.fox.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ClipboardImage;
import me.fox.components.ConfigManager;
import me.fox.config.Config;
import me.fox.config.ScreenshotConfig;
import me.fox.components.ColorPalette;
import me.fox.components.Strokes;
import me.fox.enums.LayerType;
import me.fox.ui.components.ScalableRectangle;
import me.fox.ui.components.TrayIcon;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.frames.OptionCheckboxFrame;
import me.fox.ui.frames.ScreenshotFrame;
import me.fox.utils.Util;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
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

    private final Font font = new Font("Gadugi", Font.PLAIN, 14);

    private BufferedImage image;
    private Color dimColor;
    private Color zoomCrossColor;
    private Color zoomRasterColor;
    private boolean localSave, upload, zoom;

    /**
     * Constructor for {@link ScreenshotService}
     *
     * @param screenshotFrame to initialize {@link ScreenshotService#screenshotFrame}
     *                        and {@link ScreenshotService#selectionRectangle}
     * @param drawService     to initialize {@link ScreenshotService#drawService}
     *                        and {@link ScreenshotService#selectionRectangle}
     * @param requestService  to initialize {@link ScreenshotService#requestService}
     */
    public ScreenshotService(ScreenshotFrame screenshotFrame, DrawService drawService, RequestService requestService) {
        this.drawService = drawService;
        this.screenshotFrame = screenshotFrame;
        this.drawService.registerDrawable(this, LayerType.BACKGROUND);
        this.selectionRectangle = new ScalableRectangle(drawService, screenshotFrame);
        this.requestService = requestService;
    }

    /**
     * Creates a new screenshot with the size of {@link ScreenshotService#screenshotFrame}.
     */
    public void createScreenshot() {
        try {
            this.image = new Robot().createScreenCapture(this.screenshotFrame.getBounds());
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    /**
     * Confirms a screenshot with the {@link ScreenshotService#selectionRectangle}
     * and saves it locally or uploads it.
     *
     * @param imageDetection whether it should use OCR or not
     * @param googleSearch   whether it should be searched on google or not
     * @throws IOException if an I/O error occurs
     */
    public void confirmScreenshot(boolean imageDetection, boolean googleSearch) throws IOException {
        int x = this.selectionRectangle.x;
        int y = this.selectionRectangle.y;
        int width = this.selectionRectangle.width;
        int height = this.selectionRectangle.height;
        BufferedImage screenshot = this.takeScreenshot(x, y, width, height);
        if (screenshot == null) return;

        if (this.isUpload() || imageDetection) {
            this.uploadImage(screenshot, imageDetection, googleSearch);

            if (imageDetection) return;
        }

        if (this.isLocalSave()) {
            this.saveImage(screenshot);
        }
    }

    /**
     * Generates a random name for the image file,
     * saves the image with {@link FileService#saveImage(BufferedImage, String)}
     * and copies the image to the Clipboard with {@link ClipboardImage#write(Image)}.
     *
     * @param screenshot to save
     * @throws IOException if an I/O error occurs
     */
    private void saveImage(BufferedImage screenshot) throws IOException {
        String fileName = Util.generateRandomString(18);

        Fireshotapp.getInstance().getFileService().saveImage(screenshot, fileName);
        System.out.println("Saved!");
        ClipboardImage.write(screenshot);
    }

    /**
     * Shows the user a dialog to confirm the upload to
     * the server, if it has not already been confirmed.
     * <p>
     * Cancels the upload if the user chooses the {@link JOptionPane#NO_OPTION}.
     * After the upload is complete, delete the image will be deleted.
     *
     * @param screenshot     to upload
     * @param imageDetection whether it should use OCR or not
     * @param googleSearch   whether it should be searched on google or not
     */
    private void uploadImage(BufferedImage screenshot, boolean imageDetection, boolean googleSearch) {
        if (Fireshotapp.getInstance().getJsonService().getConfig().getScreenshotConfig().isAskForUpload()) {
            Pair<Integer, Boolean> pair = OptionCheckboxFrame.
                    showDialog(null, "Fireshotapp will send the image to our server to save it for 6h. Will you allow it my lord?",
                            "Fireshotapp - Image upload");
            if (pair.getKey() == JOptionPane.NO_OPTION ||
                    pair.getKey() == JOptionPane.CANCEL_OPTION || pair.getKey() == -1) {
                Fireshotapp.getInstance().getSystemTray().info("Fireshot - Image upload",
                        "Image upload canceled");
                return;
            }

            if (pair.getValue()) {
                Fireshotapp.getInstance().getJsonService().getConfig().getScreenshotConfig().setAskForUpload(false);
                Fireshotapp.getInstance().getJsonService().saveAndApply();
            }
        }

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
                            file.delete();
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                    }
                }, Fireshotapp.getInstance().getExecutorService());
    }

    /**
     * Creates a sub-image with fitting bounds.
     *
     * @param x      selection coordinate
     * @param y      selection coordinate
     * @param width  selection width
     * @param height selection height
     * @return a {@link BufferedImage#getSubimage(int, int, int, int)}
     */
    private BufferedImage takeScreenshot(int x, int y, int width, int height) {
        if (width == 0 || height == 0 || image == null) return null;

        if (x < 0) {
            width += x;
            x = 0;
        } else if (x + width > this.image.getWidth()) {
            width = this.image.getWidth() - x;
        }

        if (y < 0) {
            height += y;
            y = 0;
        } else if (y + height > this.image.getHeight()) {
            height = this.image.getHeight() - y;
        }

        return this.image.getSubimage(x, y, width, height);
    }

    /**
     * Draws the selected part of the image so it has normal brightness.
     *
     * @param g2d       {@link Graphics2D} to draw
     * @param selection selected part
     */
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

    /**
     * Draws the zoom under the cursor and replaces it
     * if it goes out of the screen.
     *
     * @param g2d {@link Graphics2D} to draw
     */
    private void drawZoom(Graphics2D g2d) {
        ScreenshotService screenshotService = Fireshotapp.getInstance().getScreenshotService();
        Point pointerLocation = MouseInfo.getPointerInfo().getLocation();

        int screenWidth = this.screenshotFrame.getWidth();
        int screenHeight = this.screenshotFrame.getHeight();
        int screenX = this.screenshotFrame.getX();
        int screenY = this.screenshotFrame.getY();

        if (screenX < 0 || screenY < 0) {
            if (screenX < 0 && screenY < 0) {
                pointerLocation.x -= screenX;
                pointerLocation.y -= screenY;
            } else if (screenX < 0) {
                pointerLocation.x -= screenX;
            } else {
                pointerLocation.y -= screenY;
            }
        }

        if (pointerLocation.x + 160 > screenWidth) {
            pointerLocation.x -= 140;
        }
        if (pointerLocation.y + 160 > screenHeight) {
            pointerLocation.y -= 140;
        }


        Shape shape = new Ellipse2D.Float(pointerLocation.x, pointerLocation.y, 140, 140);
        g2d.setClip(shape);

        BufferedImage subImage = screenshotService.takeScreenshot(pointerLocation.x - 6,
                pointerLocation.y - 6,
                11, 11);
        if (subImage == null) return;

        g2d.drawImage(subImage, pointerLocation.x, pointerLocation.y, 140,
                140, null);

        this.drawZoomRaster(g2d, pointerLocation);
        g2d.setStroke(Strokes.WIDTH_TWO_STROKE);
        g2d.setColor(Color.WHITE);
        g2d.draw(shape);

        pointerLocation.x += 140;
        pointerLocation.y += 140;
        this.drawWidthHeightAndXY(g2d, pointerLocation);
    }

    /**
     * Draws the raster/grid for the zoom.
     *
     * @param g2d             to draw
     * @param pointerLocation as location to draw
     */
    private void drawZoomRaster(Graphics2D g2d, Point pointerLocation) {
        g2d.setColor(this.zoomCrossColor);
        g2d.fillRect(pointerLocation.x + 64, pointerLocation.y, 12, 65);
        g2d.fillRect(pointerLocation.x + 64, pointerLocation.y + 75, 12, 65);
        g2d.fillRect(pointerLocation.x, pointerLocation.y + 64, 65, 12);
        g2d.fillRect(pointerLocation.x + 75, pointerLocation.y + 64, 65, 12);

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
        g2d.setColor(Color.WHITE);
        g2d.setStroke(Strokes.WIDTH_ONE_STROKE);
        g2d.drawRect(pointerLocation.x + 64, pointerLocation.y + 64, 11, 11);
        g2d.setClip(null);
    }

    /**
     * Draws coordinate labels for the mouse location
     * and the width and height of the selection.
     *
     * @param g2d   {@link Graphics2D} to draw
     * @param point mouse location
     */
    private void drawWidthHeightAndXY(Graphics2D g2d, Point point) {
        g2d.setStroke(Strokes.WIDTH_ONE_STROKE);
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        String xy = "x: " + mouseLocation.x + "  y: " + mouseLocation.y;
        g2d.setFont(this.font);
        if (this.zoom) {
            g2d.setColor(ColorPalette.DARK_BLUE_170);
            g2d.fillRoundRect(point.x - 120, point.y + 10, xy.length() * 7, 20, 10, 10);
            g2d.setColor(ColorPalette.DARK_BLUE_LIGHTER_170);
            g2d.drawRoundRect(point.x - 120, point.y + 10, xy.length() * 7, 20, 10, 10);
            g2d.setColor(Color.white);
            g2d.drawString(xy, point.x - 110, point.y + 25);
        } else {
            g2d.setColor(ColorPalette.DARK_BLUE_170);
            g2d.fillRoundRect(point.x - 50, point.y + 15, xy.length() * 7, 20, 10, 10);
            g2d.setColor(ColorPalette.DARK_BLUE_LIGHTER_170);
            g2d.drawRoundRect(point.x - 50, point.y + 15, xy.length() * 7, 20, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString(xy, point.x - 40, point.y + 30);
        }


        if (this.selectionRectangle.x < 0 && this.selectionRectangle.y < 0) return;
        String widthHeight = Math.abs(this.selectionRectangle.width) + " x " + Math.abs(this.selectionRectangle.height);
        g2d.setColor(ColorPalette.DARK_BLUE_170);
        g2d.fillRoundRect(this.selectionRectangle.x, this.selectionRectangle.y - 25,
                widthHeight.length() * 9, 20, 10, 10);

        g2d.setColor(ColorPalette.DARK_BLUE_LIGHTER_170);
        g2d.drawRoundRect(this.selectionRectangle.x, this.selectionRectangle.y - 25,
                widthHeight.length() * 9, 20, 10, 10);
        g2d.setColor(Color.WHITE);
        g2d.drawString(widthHeight, this.selectionRectangle.x + 10, this.selectionRectangle.y - 10);
    }

    @Override
    public void applyConfig(Config config) {
        ScreenshotConfig screenshotConfig = config.getScreenshotConfig();
        Color color = Color.decode(screenshotConfig.getDimColor());
        this.dimColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 120);
        color = Color.decode(screenshotConfig.getZoomCrossColor());
        this.zoomCrossColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 77);
        color = Color.decode(screenshotConfig.getZoomRasterColor());
        this.zoomRasterColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 100);
        this.localSave = screenshotConfig.isLocalSave();
        this.upload = screenshotConfig.isUpload();
        this.zoom = screenshotConfig.isZoom();
        TrayIcon trayIcon = Fireshotapp.getInstance().getSystemTray();
        trayIcon.getLocalSaveItem().setState(this.localSave);
        trayIcon.getUploadItem().setState(this.upload);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (this.image != null) {
            int width = this.screenshotFrame.getWidth();
            int height = this.screenshotFrame.getHeight();
            g2d.drawImage(this.image, 0, 0, width, height, null);

            //Draw black overlay
            g2d.setColor(this.dimColor);
            g2d.fillRect(0, 0, width, height);

            //Draw selected image part
            this.drawSelected(g2d, this.selectionRectangle);

            if (this.zoom && !this.drawService.isDraw()) {
                this.drawZoom(g2d);
            } else if (!this.zoom && !this.drawService.isDraw()) {
                Point pointerLocation = MouseInfo.getPointerInfo().getLocation();
                this.drawWidthHeightAndXY(g2d, pointerLocation);
            }
        }
    }
}