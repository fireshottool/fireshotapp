package me.fox.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.fox.components.Hotkey;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

public class JsonService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileSeparator = System.getProperty("file.separator");
    private final Path jsonPath = Path.of(System.getProperty("user.home") + fileSeparator + "fireshot" + fileSeparator + "test.json");

    private JsonObject jsonObject;

    public void read(HotkeyService hotkeyService, DrawService drawService, ScreenshotService screenshotService) {
        CompletableFuture.runAsync(() -> {
            try {
                FileInputStream fileInputStream = new FileInputStream(jsonPath.toString());
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

                this.jsonObject = gson.fromJson(inputStreamReader, JsonObject.class);

                readHotkeys(hotkeyService);
                readScreenshot(screenshotService);
                readDraw(drawService);

                fileInputStream.close();
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void readScreenshot(ScreenshotService screenshotService) {
        JsonObject screenshotObject = this.jsonObject.getAsJsonObject("screenshot");
        screenshotService.setScreenshotPath(Path.of(screenshotObject.get("imageLocation").getAsString()));
        screenshotService.setDimColor(Color.decode(screenshotObject.get("dimColor").getAsString()));
    }

    private void readHotkeys(HotkeyService hotkeyService) {
        List<Hotkey> list = new ArrayList<>();

        JsonArray jsonArray = this.jsonObject.getAsJsonArray("hotkeys");

        jsonArray.forEach(var -> list.add(gson.fromJson(var, Hotkey.class)));
        hotkeyService.getHotkeys().addAll(list);
    }

    private void readDraw(DrawService drawService) {
        JsonObject drawObject = this.jsonObject.getAsJsonObject("draw");
        drawService.setCurrentStrokeWidth(drawObject.get("defaultThickness").getAsFloat());
        drawService.setIncreaseThickness(drawObject.get("thicknessIncrease").getAsFloat());
        drawService.setIncreaseThickness(drawObject.get("thicknessDecrease").getAsFloat());
        drawService.setDrawColor(Color.decode(drawObject.get("color").getAsString()));
    }
}