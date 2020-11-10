package me.fox;

import lombok.Getter;
import me.fox.services.*;
import me.fox.ui.frames.ScreenshotFrame;

import javax.swing.*;

/**
 * @author (Ausgefuchster)
 * @version (2.0 ~ 21.10.2020)
 */

@Getter
public class Fireshot {
    @Getter
    private static Fireshot instance;

    private final DrawService drawService = new DrawService();
    private final JsonService jsonService = new JsonService();
    private final ScreenshotFrame screenshotFrame = new ScreenshotFrame("Fireshot", this.drawService);
    private final ScreenshotService screenshotService = new ScreenshotService(screenshotFrame, drawService);
    private final ScreenService screenService = new ScreenService(screenshotFrame, screenshotService);
    private final HotkeyService hotkeyService = new HotkeyService(screenshotService, drawService, screenService);

    private void load(String[] args) {
        this.jsonService.read(hotkeyService, drawService, screenshotService);
        this.screenshotFrame.registerMouseListener(this.drawService.getDrawListener());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = new Fireshot();
        instance.load(args);
    }
}
