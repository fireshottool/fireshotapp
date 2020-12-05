package me.fox.services;

import lombok.Setter;
import me.fox.components.ConfigManager;
import me.fox.components.ResourceManager;
import me.fox.config.Config;
import me.fox.config.FileConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Setter
public class FileService implements ConfigManager {

    private final String fileSeparator = System.getProperty("file.separator");
    private final Path resourcePath = Path.of(System.getProperty("user.home") +
            fileSeparator + "fireshot" + fileSeparator + "resources");

    private final List<File> resources = new ArrayList<>();
    private final List<ResourceManager> resourceManagers = new ArrayList<>();

    private Path screenshotPath = Path.of(System.getProperty("user.home") +
            fileSeparator + "fireshot" + fileSeparator + "screenshots");
    private String imageType = "png";

    public FileService(ResourceManager... resourceManager) {
        this.resourceManagers.addAll(List.of(resourceManager));
        this.loadResources();
    }

    public void saveImage(BufferedImage image, String fileName) throws IOException {
        if (!Files.exists(this.screenshotPath)) {
            Files.createDirectories(screenshotPath);
        }
        File file = new File(this.screenshotPath.toString() + fileSeparator + fileName + "." + this.imageType);
        ImageIO.write(image, this.imageType, file);
    }

    public void loadResources() {
        File file = resourcePath.toFile();
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles(File::isFile);
            if (files != null) {
                this.resources.addAll(List.of(files));
                this.invokeResourceManager();
                return;
            }
        }
        this.downLoadResources();
    }

    private void downLoadResources() {
        System.out.println("downloading...");
    }

    private void invokeResourceManager() {
        System.out.println(resourceManagers);
        this.resourceManagers.forEach(var -> var.applyResources(this.resources));
    }

    @Override
    public void applyConfig(Config config) {
        FileConfig fileConfig = config.getFileConfig();
        this.screenshotPath = Path.of(fileConfig.getImageLocation());
        this.imageType = fileConfig.getImageType();
    }
}
