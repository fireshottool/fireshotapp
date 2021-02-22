package me.fox.services;

import com.google.common.collect.Lists;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ConfigManager;
import me.fox.components.ResourceManager;
import me.fox.config.Config;
import me.fox.config.FileConfig;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Setter
public class FileService implements ConfigManager {

    private final String fileSeparator = System.getProperty("file.separator");
    private final String resourcePath = System.getenv("LOCALAPPDATA") +
            fileSeparator + "Programs" + fileSeparator + fileSeparator +
            "Fireshotapp" + fileSeparator + "data" + fileSeparator + "resources" + fileSeparator;

    private final List<File> resources = new ArrayList<>();
    private final List<String> requiredFiles = Lists.newArrayList(
            "decrease.png", "increase.png", "pencil.png", "pencilw.png", "redo.png", "undo.png",
            "save.png", "trayIcon.png", "ocr.png", "googlesearch.png", "cross.png"
    );
    private final List<ResourceManager> resourceManagers = new ArrayList<>();

    private Path screenshotPath = Paths.get(System.getProperty("user.home") +
            fileSeparator + "fireshot" + fileSeparator + "screenshots");
    private String imageType = "png";

    /**
     * Constructor for {@link FileService}
     *
     * @param resourceManager to add to {@link FileService#resourceManagers}.
     */
    public FileService(ResourceManager... resourceManager) {
        this.resourceManagers.addAll(Lists.newArrayList(resourceManager));
    }

    /**
     * Saves an image, if the directories exist.
     *
     * @param image    to save
     * @param fileName name of the image
     * @throws IOException if an I/O error occurs
     */
    public void saveImage(BufferedImage image, String fileName) throws IOException {
        if (!Files.exists(this.screenshotPath)) {
            Files.createDirectories(this.screenshotPath);
        }
        File file = new File(this.screenshotPath.toString() + this.fileSeparator + fileName + "." + this.imageType);
        ImageIO.write(image, this.imageType, file);
    }

    /**
     * Clears all {@link FileService#resources} and
     * loads all files from the {@link FileService#resourcePath}.
     */
    public void loadResources() {
        this.resources.clear();
        File file = new File(this.resourcePath);
        if (file.exists()) {
            File[] files = file.listFiles(File::isFile);
            if (files != null) {
                this.resources.addAll(Lists.newArrayList(files));
                this.invokeResourceManager();
            }
        } else {
            try {
                Files.createDirectories(file.toPath());
                this.invokeResourceManager();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if all {@link FileService#requiredFiles} are loaded and
     * invokes all {@link FileService#resourceManagers} with {@link ResourceManager#applyResources(List)}.
     */
    private void invokeResourceManager() {
        this.checkRequiredFiles();
        this.resourceManagers.forEach(var -> var.applyResources(this.resources));
    }

    /**
     * Checks if all {@link FileService#requiredFiles} could be loaded into {@link FileService#resources}.
     */
    private void checkRequiredFiles() {
        this.requiredFiles.stream()
                .filter(var -> this.resources.stream().noneMatch(file -> file.getName().equals(var)))
                .forEach(var -> this.resources.add(this.getAndWriteImage(var)));
    }

    /**
     * Downloads an image from the server and writes it into the {@link FileService#resourcePath}.
     *
     * @param imageName to get the image from the server
     * @return the file from the image
     */
    private File getAndWriteImage(String imageName) {
        RequestService requestService = Fireshotapp.getInstance().getRequestService();
        try {
            Image image = requestService.requestImage(imageName).get();
            Objects.requireNonNull(image);
            ImageIO.write((RenderedImage) image, "png",
                    new File(this.resourcePath + imageName));
        } catch (InterruptedException | IOException | ExecutionException e) {
            e.printStackTrace();
        }
        return new File(this.resourcePath + imageName);
    }

    @Override
    public void applyConfig(Config config) {
        FileConfig fileConfig = config.getFileConfig();
        this.screenshotPath = Paths.get(fileConfig.getImageLocation());
        this.imageType = fileConfig.getImageType();
    }
}
