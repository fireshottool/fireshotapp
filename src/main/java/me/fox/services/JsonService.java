package me.fox.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.fox.config.Config;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

public class JsonService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileSeparator = System.getProperty("file.separator");
    private final Path jsonPath = Path.of(System.getProperty("user.home") +
            fileSeparator + "fireshot" + fileSeparator + "test.json");

    private Config config;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void read(HotkeyService hotkeyService,
                     DrawService drawService,
                     ScreenshotService screenshotService,
                     FileService fileService) {

        CompletableFuture.runAsync(() -> {
            try {
                File file = this.jsonPath.toFile();

                if (!file.exists()) {
                    if (file.getParentFile() != null) {
                        file.getParentFile().mkdirs();
                    }
                    file.createNewFile();

                    FileWriter writer = new FileWriter(file);
                    this.gson.toJson(Config.DEFAULT_CONFIG, writer);

                    writer.flush();
                    writer.close();
                }

                this.config = this.gson.fromJson(new BufferedReader(new FileReader(file)), Config.class);
                this.applyConfig(hotkeyService, drawService, screenshotService, fileService);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void applyConfig(HotkeyService hotkeyService,
                             DrawService drawService,
                             ScreenshotService screenshotService,
                             FileService fileService) {
        hotkeyService.applyConfig(this.config.getHotkeyConfig());
        drawService.applyConfig(this.config.getDrawConfig());
        screenshotService.applyConfig(this.config.getScreenshotConfig());
        fileService.applyConfig(this.config.getFileConfig());
    }
}