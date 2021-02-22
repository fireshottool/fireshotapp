package me.fox.services;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.fox.components.ConfigManager;
import me.fox.config.Config;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

@Getter
public class JsonService {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String fileSeparator = System.getProperty("file.separator");
    private final Path jsonPath = Paths.get(System.getenv("LOCALAPPDATA") +
            fileSeparator + "Programs" + fileSeparator + fileSeparator +
            "Fireshotapp" + fileSeparator + "data" + fileSeparator + "fireshot.json");

    private final List<ConfigManager> configManagers = new ArrayList<>();

    private Config config;

    /**
     * Reads the config file - {@link JsonService#jsonPath}.
     * If the file does not exist, or if it is empty, a default
     * {@link Config} {@link Config#DEFAULT_CONFIG} is created.
     * <p>
     * When the config is loaded, all {@link ConfigManager} are invoked.
     *
     * @param configManagers to invoke
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void read(ConfigManager... configManagers) {
        this.configManagers.addAll(Lists.newArrayList(configManagers));
        try {
            File file = this.jsonPath.toFile();

            if (!file.exists()) {
                if (file.getParentFile() != null) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();

                this.config = this.createDefault(file);
                Arrays.stream(configManagers).forEach(var -> var.applyConfig(this.config));
                return;
            }

            this.config = this.gson.fromJson(new BufferedReader(new FileReader(file)), Config.class);
            if (this.config == null)
                this.config = this.createDefault(file);

            this.invokeConfigManager();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current {@link JsonService#config} and reads
     * it to invoke all {@link JsonService#configManagers}.
     */
    public void saveAndApply() {
        this.save();
        this.invokeConfigManager();
    }

    /**
     * Invokes all {@link JsonService#configManagers}.
     */
    private void invokeConfigManager() {
        this.configManagers.forEach(var -> var.applyConfig(this.config));
    }

    /**
     * Saves the current {@link JsonService#config} to the {@link JsonService#jsonPath}.
     * Creates the file and folders if they do not exist.
     */
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

    /**
     * Creates the {@link Config#DEFAULT_CONFIG}.
     *
     * @param file to create the default config
     * @return the default config
     * @throws IOException if there was a problem writing to the writer
     * @see Gson#toJson(Object, Appendable)
     */
    private Config createDefault(File file) throws IOException {
        FileWriter writer = new FileWriter(file);
        Config config = Config.DEFAULT_CONFIG;
        this.gson.toJson(config, writer);

        writer.flush();
        writer.close();

        return config;
    }
}