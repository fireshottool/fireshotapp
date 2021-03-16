package me.fox.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import javafx.util.Pair;
import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ClipboardImage;
import me.fox.components.ConfigManager;
import me.fox.config.Config;
import me.fox.config.ScreenshotConfig;
import me.fox.constants.ColorPalette;
import me.fox.enums.LayerType;
import me.fox.ui.components.ScalableRectangle;
import me.fox.ui.components.TrayIcon;
import me.fox.ui.components.draw.Drawable;
import me.fox.ui.components.draw.impl.ImageZoom;
import me.fox.ui.frames.OptionCheckboxFrame;
import me.fox.ui.frames.ScreenshotFrame;
import org.apache.commons.lang3.RandomStringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
@Setter
public class ScreenshotService implements Service, Drawable, ConfigManager {

    private final ScreenshotFrame screenshotFrame;
    private final ScalableRectangle selectionRectangle;
    private final DrawService drawService;
    private final RequestService requestService;

    private final Font font = new Font("Gadugi", Font.PLAIN, 14);
    private final ImageZoom imageZoom = new ImageZoom();

    private BufferedImage image;
    private Color dimColor;
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
        BufferedImage screenshot = this.takeScreenshotFromSelection();
        if (screenshot == null) return;

        Graphics2D g2d = this.image.createGraphics();
        this.drawService.drawDrawings(g2d);
        if (this.isUpload() || imageDetection || googleSearch) {
            this.uploadImage(screenshot, imageDetection, googleSearch);

            if (imageDetection) return;
        }

        if (this.isLocalSave()) {
            this.saveImage(screenshot);
        }
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
    public BufferedImage takeScreenshot(int x, int y, int width, int height) {
        if (width == 0 || height == 0 || this.image == null) return null;

        Rectangle corrected = this.correctImageCoordinates(x, y, width, height);

        return this.image.getSubimage(corrected.x, corrected.y, corrected.width, corrected.height);
    }

    public BufferedImage takeScreenshotFromSelection() {
        return this.takeScreenshot(
                this.selectionRectangle.x, this.selectionRectangle.y,
                this.selectionRectangle.width, this.selectionRectangle.height
        );
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
        String fileName = RandomStringUtils.randomAlphabetic(18);

        Fireshotapp.getInstance().use(FileService.class).saveImage(screenshot, fileName);
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
        if (this.askUploadIfSetInConfig(imageDetection, googleSearch)) {
            File file = new File("image.png");

            this.writeScreenshotToFile(screenshot, file);
            this.deleteAfterUpload(file, imageDetection, googleSearch);
        }
    }

    private void writeScreenshotToFile(BufferedImage screenshot, File file) {
        try {
            ImageIO.write(screenshot, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteAfterUpload(File file, boolean imageDetection, boolean googleSearch) {
        ListenableFuture<File> future = this.requestService.uploadImage(new File(file.toString()), imageDetection, googleSearch);
        Futures.addCallback(future, this.deleteFileCallback(), Fireshotapp.getInstance().getExecutorService());
    }

    private FutureCallback<File> deleteFileCallback() {
        return new FutureCallback<File>() {
            @Override
            public void onSuccess(@Nullable File file) {
                if (file != null) {
                    file.delete();
                }
            }

            @Override
            public void onFailure(@Nonnull Throwable throwable) {
            }
        };
    }

    private boolean askUploadIfSetInConfig(boolean imageDetection, boolean googleSearch) {
        if (this.shouldAskForUpload()) {
            Pair<Integer, Boolean> result = this.showUploadDialog(imageDetection, googleSearch);

            if (this.isCancelOption(result.getKey())) {
                this.showUploadCanceledInfo();
                return false;
            }

            if (result.getValue()) {
                this.saveNoUploadToConfig();
            }
        }
        return true;
    }

    private boolean shouldAskForUpload() {
        return Fireshotapp.getInstance().use(JsonService.class).getConfig().getScreenshotConfig().isAskForUpload();
    }

    private Pair<Integer, Boolean> showUploadDialog(boolean imageDetection, boolean googleSearch) {
        String message = googleSearch ?
                "Fireshot will send the image to the server to save it for 6h and use the image URL for google Search. Is that ok for you?" :
                imageDetection ?
                        "Fireshotapp will send the image to the server to use Tesseract to get the text (OCR). Is that ok for you?" :
                        "Fireshotapp will send the image to the server to save it for 6h. Is that ok for you?";
        return OptionCheckboxFrame.showDialog(
                Fireshotapp.getInstance().getScreenshotFrame(),
                message,
                "Fireshotapp - Image upload"
        );
    }

    private void saveNoUploadToConfig() {
        JsonService jsonService = Fireshotapp.getInstance().use(JsonService.class);
        jsonService.getConfig().getScreenshotConfig().setAskForUpload(false);
        jsonService.saveAndApply();
    }

    private void showUploadCanceledInfo() {
        Fireshotapp.getInstance().getSystemTray().info(
                "Fireshot - Image upload",
                "Image upload canceled"
        );

    }

    private boolean isCancelOption(int key) {
        return key == JOptionPane.NO_OPTION || key == JOptionPane.CANCEL_OPTION || key == JOptionPane.DEFAULT_OPTION;
    }

    private Rectangle correctImageCoordinates(int x, int y, int width, int height) {
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
        return new Rectangle(x, y, width, height);
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

        if (width == 0 || height == 0) return;

        if (selection.width < 0) {
            x -= width;
        }

        if (selection.height < 0) {
            y -= height;
        }

        BufferedImage overlayScreen = this.takeScreenshot(x, y, width, height);
        g2d.drawImage(overlayScreen, Math.max(x, 0), Math.max(y, 0), null);
        this.drawSelectionInfo(g2d);
    }

    private void drawSelectionInfo(Graphics2D g2d) {
        if (this.selectionRectangle.x <= 0 || this.selectionRectangle.y <= 0) return;
        String widthHeight = Math.abs(this.selectionRectangle.width) + " x " + Math.abs(this.selectionRectangle.height);

        g2d.setColor(ColorPalette.DARK_BLUE_170);
        g2d.fillRoundRect(
                this.selectionRectangle.x, this.selectionRectangle.y - 25,
                widthHeight.length() * 9, 20, 10, 10
        );

        g2d.setColor(ColorPalette.DARK_BLUE_LIGHTER_170);
        g2d.drawRoundRect(
                this.selectionRectangle.x, this.selectionRectangle.y - 25,
                widthHeight.length() * 9, 20, 10, 10
        );

        g2d.setColor(Color.WHITE);
        g2d.drawString(widthHeight, this.selectionRectangle.x + 10, this.selectionRectangle.y - 10);
    }

    private void drawImageWithScreenshotFrame(Graphics2D g2d) {
        g2d.drawImage(this.image, 0, 0, this.screenshotFrame.getWidth(), this.screenshotFrame.getHeight(), null);
    }

    private void drawOverlay(Graphics2D g2d) {
        g2d.setColor(this.dimColor);
        g2d.fillRect(0, 0, this.screenshotFrame.getWidth(), this.screenshotFrame.getHeight());
    }

    private void applyFieldsFromConfig(ScreenshotConfig config) {
        this.localSave = config.isLocalSave();
        this.upload = config.isUpload();
        this.zoom = config.isZoom();

        this.imageZoom.setGrid(config.isZoomGrid());
        this.imageZoom.setCross(config.isZoomCross());
        this.imageZoom.setMiddleRect(config.isZoomMiddleRect());
    }

    private void applyTrayIconStateFromConfig(ScreenshotConfig config) {
        TrayIcon trayIcon = Fireshotapp.getInstance().getSystemTray();
        trayIcon.getLocalSaveItem().setState(config.isLocalSave());
        trayIcon.getUploadItem().setState(config.isUpload());
    }

    private void applyColorsFromConfig(ScreenshotConfig config) {
        Color color = Color.decode(config.getDimColor());
        this.dimColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 120);

        color = Color.decode(config.getZoomCrossColor());
        this.imageZoom.setCrossColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 77));

        color = Color.decode(config.getZoomGridColor());
        this.imageZoom.setGridColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 50));

        color = Color.decode(config.getZoomBorderColor());
        this.imageZoom.setBorderColor(color);

        color = Color.decode(config.getZoomMiddleRectColor());
        this.imageZoom.setMiddleRectColor(color);
    }

    @Override
    public void applyConfig(Config config) {
        ScreenshotConfig conf = config.getScreenshotConfig();
        this.applyColorsFromConfig(conf);
        this.applyFieldsFromConfig(conf);
        this.applyTrayIconStateFromConfig(conf);
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (this.image == null) {
            return;
        }
        this.drawImageWithScreenshotFrame(g2d);
        this.drawOverlay(g2d);
        this.drawSelected(g2d, this.selectionRectangle);
        this.imageZoom.draw(g2d);
    }
}