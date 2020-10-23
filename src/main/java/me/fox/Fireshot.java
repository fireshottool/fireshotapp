package me.fox;

import lombok.Getter;
import me.fox.services.DrawService;
import me.fox.services.HotkeyService;
import me.fox.services.JsonService;
import me.fox.services.ScreenshotService;
import me.fox.ui.frames.ScreenshotFrame;

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
    private final HotkeyService hotkeyService = new HotkeyService(screenshotService);

    private void load(String[] args) {
        this.jsonService.read(hotkeyService);
    }

    public static void main(String[] args) {
        instance = new Fireshot();
        instance.load(args);
    }
}
