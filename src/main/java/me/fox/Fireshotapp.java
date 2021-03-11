package me.fox;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import me.fox.components.Version;
import me.fox.services.*;
import me.fox.ui.components.TrayIcon;
import me.fox.ui.frames.ScreenshotFrame;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author (Ausgefuchster)
 * @version (2.0 ~ 21.10.2020)
 */

@Getter
public class Fireshotapp {

    @Getter
    private static Fireshotapp instance;

    public static final Version VERSION = new Version("0.3.3");
    private final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(2));

    private final Map<Class<? extends Service>, Service> services = new HashMap<>();

    private final TrayIcon systemTray = new TrayIcon("Fireshot");
    private final ScreenshotFrame screenshotFrame = new ScreenshotFrame("Fireshotapp");

    @SuppressWarnings("unchecked")
    public <T extends Service> T use(Class<? super T> type) {
        return (T) this.services.get(type);
    }

    private void registerServices(Service... services) {
        Arrays.stream(services).forEach((service) -> this.services.put(service.getClass(), service));
    }

    private void registerServices() {
        DrawService drawService = new DrawService();

        this.screenshotFrame.setContentPane(drawService);
        JsonService jsonService = new JsonService();
        RequestService requestService = new RequestService();
        ScreenshotService screenshotService = new ScreenshotService(this.screenshotFrame, drawService, requestService);
        ScreenService screenService = new ScreenService(this.screenshotFrame, screenshotService);
        HotkeyService hotkeyService = new HotkeyService(screenshotService, drawService, screenService);
        FileService fileService = new FileService(screenService, this.systemTray, drawService);
        UpdateService updateService = new UpdateService(requestService, jsonService);

        this.registerServices(
                drawService,
                jsonService,
                requestService,
                screenshotService,
                screenService,
                hotkeyService,
                fileService,
                updateService
        );

    }

    private void load() {
        this.registerServices();
        this.readJson();
        this.use(FileService.class).loadResources();
        this.use(UpdateService.class).checkForUpdate(false);
        this.screenshotFrame.registerMouseListener(this.use(DrawService.class).getDrawListener());
    }

    private void readJson() {
        this.use(JsonService.class).read(this.services.values());
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        instance = new Fireshotapp();
        instance.load();
    }
}
