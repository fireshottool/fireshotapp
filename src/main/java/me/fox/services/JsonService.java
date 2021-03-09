package me.fox.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import me.fox.components.ConfigManager;
import me.fox.config.Config;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

@Getter
public class JsonService implements Service {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path jsonPath = Paths.get(
            System.getenv("LOCALAPPDATA"), "Programs", "Fireshotapp", "fireshot.json"
    );

    private final List<ConfigManager> configManagers = new ArrayList<>();

    private Config config;

    /**
     * Reads the config file - {@link JsonService#jsonPath}.
     * If the file does not exist, or if it is empty, a default
     * {@link Config} {@link Config#DEFAULT_CONFIG} is created.
     * <p>
     * When the config is loaded, all {@link ConfigManager} are invoked.
     *
     * @param services to add to {@link JsonService#configManagers}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void read(Collection<Service> services) {
        if (services != null)
            this.configManagers.addAll(services);
        try {
            File file = this.jsonPath.toFile();

            if (!file.exists()) {
                this.createFileAndParents(file);

                this.config = this.createDefault(file);
                this.configManagers.forEach(var -> var.applyConfig(this.config));
            } else {
                this.readConfig(file);
                this.invokeConfigManager();
            }
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

    private void createFileAndParents(File file) throws IOException {
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();
    }

    private void readConfig(File file) throws IOException {
        this.config = this.gson.fromJson(new BufferedReader(new FileReader(file)), Config.class);
        if (this.config == null)
            this.config = this.createDefault(file);
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
                this.createFileAndParents(file);
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

    @Override
    public void applyConfig(Config config) {
    }
}