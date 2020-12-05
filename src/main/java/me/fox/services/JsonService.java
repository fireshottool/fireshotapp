package me.fox.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.fox.components.ConfigManager;
import me.fox.config.Config;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

@Getter
public class JsonService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileSeparator = System.getProperty("file.separator");
    private final Path jsonPath = Path.of(System.getProperty("user.home") +
            fileSeparator + "fireshot" + fileSeparator + "test.json");

    private final List<ConfigManager> configManagers = new ArrayList<>();

    private Config config;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void read(ConfigManager... configManagers) {
        this.configManagers.addAll(List.of(configManagers));

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

                Arrays.stream(configManagers).forEach(var -> var.applyConfig(this.config));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void save() {
        try {
            File file = this.jsonPath.toFile();

            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            this.gson.toJson(this.config, writer);

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAndApply() {
        this.save();
        this.read(this.configManagers.toArray(ConfigManager[]::new));
    }

    public void registerConfigManagers(ConfigManager... configManagers) {
        this.configManagers.addAll(List.of(configManagers));
    }
}