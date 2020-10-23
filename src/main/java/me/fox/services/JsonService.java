package me.fox.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.fox.components.Hotkey;

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

    public void read(HotkeyService hotkeyService) {
        CompletableFuture.runAsync(() -> {
            try {
                FileInputStream fileInputStream = new FileInputStream(jsonPath.toString());
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

                this.jsonObject = gson.fromJson(inputStreamReader, JsonObject.class);

                readHotkeys(hotkeyService);

                fileInputStream.close();
                inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void readHotkeys(HotkeyService hotkeyService) {
        List<Hotkey> list = new ArrayList<>();

        JsonArray jsonArray = this.jsonObject.getAsJsonArray("hotkeys");

        jsonArray.forEach(var -> list.add(gson.fromJson(var, Hotkey.class)));
        hotkeyService.getHotkeys().addAll(list);
    }
}