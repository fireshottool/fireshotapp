package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshot;
import me.fox.ui.frames.ScreenshotFrame;
import me.fox.ui.frames.SettingsFrame;
import me.fox.ui.panels.toolbox.Toolbox;
import me.fox.ui.panels.toolbox.ext.DrawToolbox;
import me.fox.ui.panels.toolbox.ext.ScreenshotToolbox;

import java.io.IOException;

/**
 * @author (Ausgefuchster)
 * @version (~ 24.10.2020)
 */

@Getter
@Setter
public class ScreenService {

    private final ScreenshotFrame screenshotFrame;
    private final ScreenshotService screenshotService;
    private final Toolbox drawToolbox = new DrawToolbox();
    private final Toolbox screenshotToolbox = new ScreenshotToolbox();
    private final SettingsFrame settingsFrame = new SettingsFrame();

    public ScreenService(ScreenshotFrame screenshotFrame, ScreenshotService screenshotService) {
        this.screenshotFrame = screenshotFrame;
        this.screenshotService = screenshotService;
        this.screenshotFrame.add(drawToolbox);
        this.screenshotFrame.add(screenshotToolbox);
    }

    public void show() {
        this.screenshotService.createScreenshot();
        this.screenshotFrame.setVisible(true);
    }

    public void hideAndConfirm() {
        this.screenshotFrame.setVisible(false);
        try {
            this.screenshotService.confirmScreenShot();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.resetAndHide();
    }

    public void resetAndHide() {
        this.screenshotService.setImage(null);
        this.screenshotService.getSelectionRectangle().setRect(-10, -10, 0, 0);
        this.screenshotFrame.setVisible(false);
        DrawService drawService = Fireshot.getInstance().getDrawService();
        drawToolbox.reset();
        screenshotToolbox.reset();
        drawService.resetDraw();
        this.drawToolbox.hideSelf();
        this.screenshotToolbox.hideSelf();

    }

}
