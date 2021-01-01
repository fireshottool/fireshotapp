package me.fox;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import me.fox.services.*;
import me.fox.ui.components.TrayIcon;
import me.fox.ui.frames.ScreenshotFrame;

import javax.swing.*;
import java.util.concurrent.Executors;

/**
 * @author (Ausgefuchster)
 * @version (2.0 ~ 21.10.2020)
 */

@Getter
public class Fireshot {

    @Getter
    private static Fireshot instance;

    private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(2));

    private final DrawService drawService = new DrawService();
    private final JsonService jsonService = new JsonService();
    private final ScreenshotFrame screenshotFrame = new ScreenshotFrame("Fireshot", drawService);
    private final RequestService requestService = new RequestService();
    private final ScreenshotService screenshotService = new ScreenshotService(screenshotFrame, drawService, requestService);
    private final ScreenService screenService = new ScreenService(screenshotFrame, screenshotService);
    private final HotkeyService hotkeyService = new HotkeyService(screenshotService, drawService, screenService);
    private final TrayIcon systemTray = new TrayIcon("Fireshot");
    private final FileService fileService = new FileService(screenService, systemTray, drawService);

    private void load(String[] args) {
        this.readJson();
        this.fileService.loadResources();
        this.screenshotFrame.registerMouseListener(this.drawService.getDrawListener());
    }

    private void readJson() {
        this.jsonService.read(
                hotkeyService,
                drawService,
                screenshotService,
                fileService,
                requestService,
                screenService.getSettingsFrame()
        );
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
