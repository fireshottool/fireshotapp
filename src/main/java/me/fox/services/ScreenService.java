package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ResourceManager;
import me.fox.config.Config;
import me.fox.ui.frames.ScreenshotFrame;
import me.fox.ui.frames.SettingsFrame;
import me.fox.ui.panels.toolbox.Toolbox;
import me.fox.ui.panels.toolbox.ext.DrawToolbox;
import me.fox.ui.panels.toolbox.ext.ScreenshotToolbox;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
@Setter
public class ScreenService implements Service, ResourceManager {

    private final ScreenshotFrame screenshotFrame;
    private final ScreenshotService screenshotService;
    private final Toolbox drawToolbox;
    private final Toolbox screenshotToolbox = new ScreenshotToolbox();
    private final SettingsFrame settingsFrame = new SettingsFrame();

    /**
     * Constructor for {@link ScreenService}
     * Adds {@link ScreenService#drawToolbox} and {@link ScreenService#screenshotToolbox}
     * to {@link ScreenService#screenshotFrame}.
     *
     * @param screenshotFrame   to initialize {@link ScreenService#screenshotFrame}
     *                          and {@link ScreenService#drawToolbox}
     * @param screenshotService to initialize {@link ScreenService#screenshotService}
     */
    public ScreenService(ScreenshotFrame screenshotFrame, ScreenshotService screenshotService) {
        this.screenshotFrame = screenshotFrame;
        this.screenshotService = screenshotService;
        this.drawToolbox = new DrawToolbox(screenshotFrame);
        this.screenshotFrame.add(this.drawToolbox);
        this.screenshotFrame.add(this.screenshotToolbox);
    }

    /**
     * Shows the frame to the user.
     *
     * @see ScreenshotService#createScreenshot()
     */
    public void show() {
        this.screenshotService.createScreenshot();
        this.screenshotFrame.setVisible(true);
        this.screenshotFrame.requestFocus();
    }

    /**
     * Hides the {@link ScreenService#screenshotFrame} and confirms the screenshot.
     *
     * @param imageDetection whether to use OCR or not
     * @param googleSearch   whether to use google image search or not
     */
    public void hideAndConfirm(boolean imageDetection, boolean googleSearch) {
        try {
            this.screenshotService.confirmScreenshot(imageDetection, googleSearch);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.resetAndHide();
    }

    /**
     * Resets the screen for image selection and all values.
     * Hides the screen again.
     */
    public void resetAndHide() {
        this.reset();
        this.hide();
    }

    private void reset() {
        this.screenshotService.setImage(null);
        this.screenshotService.getSelectionRectangle().setRect(-10, -10, 0, 0);

        Arrays.stream(this.screenshotService.getSelectionRectangle().getScalePoints())
                .forEach(var -> var.updateLocation(this.screenshotService.getSelectionRectangle()));

        this.drawToolbox.reset();
        this.screenshotToolbox.reset();

        Fireshotapp.getInstance().use(DrawService.class).resetDraw();
    }

    private void hide() {
        this.screenshotFrame.setVisible(false);
        this.drawToolbox.hideSelf();
        this.screenshotToolbox.hideSelf();
    }

    @Override
    public void applyResources(List<File> files) {
        drawToolbox.applyResources(files);
        this.screenshotToolbox.applyResources(files);
    }

    @Override
    public void applyConfig(Config config) {
        this.settingsFrame.applyConfig(config);
    }
}