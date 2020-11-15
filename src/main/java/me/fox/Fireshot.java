package me.fox;

import lombok.Getter;
import me.fox.services.*;
import me.fox.ui.components.TrayIcon;
import me.fox.ui.frames.ScreenshotFrame;

import javax.swing.*;
import java.awt.image.BufferedImage;

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
    private final FileService fileService = new FileService();
    private final ScreenshotFrame screenshotFrame = new ScreenshotFrame("Fireshot", this.drawService);
    private final ScreenshotService screenshotService = new ScreenshotService(this.screenshotFrame, this.drawService);
    private final ScreenService screenService = new ScreenService(this.screenshotFrame, this.screenshotService);
    private final HotkeyService hotkeyService = new HotkeyService(this.screenshotService, this.drawService, this.screenService);
    private final TrayIcon systemTray = new TrayIcon(new BufferedImage(32, 32, 1), "Fireshot");

    private void load(String[] args) {
        this.jsonService.read(hotkeyService, drawService, screenshotService, fileService);
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
