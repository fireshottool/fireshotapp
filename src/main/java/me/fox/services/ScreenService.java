package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.ui.frames.ScreenshotFrame;

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

    public ScreenService(ScreenshotFrame screenshotFrame, ScreenshotService screenshotService) {
        this.screenshotFrame = screenshotFrame;
        this.screenshotService = screenshotService;
    }

    public void show() {
        this.screenshotService.createScreenshot();
        this.screenshotFrame.setVisible(true);
    }

    public void confirmAndHide() {
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
        this.screenshotService.getDrawService().resetDraw();
    }

}
