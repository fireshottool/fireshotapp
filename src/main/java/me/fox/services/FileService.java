package me.fox.services;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import lombok.Setter;
import me.fox.Fireshot;
import me.fox.components.ConfigManager;
import me.fox.components.ResourceManager;
import me.fox.config.Config;
import me.fox.config.FileConfig;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Setter
public class FileService implements ConfigManager {

    private final String fileSeparator = System.getProperty("file.separator");
    private final String resourcePath = System.getProperty("user.home") +
            fileSeparator + "fireshot" + fileSeparator + "resources" + fileSeparator;

    private final List<File> resources = new ArrayList<>();
    private final List<String> requiredFiles = List.of(
            "decrease.png", "increase.png", "pencil.png", "redo.png", "undo.png",
            "save.png", "toolboxbg.png", "trayIcon.png", "ocr.png", "googlesearch.png", "cross.png"
    );
    private final List<ResourceManager> resourceManagers = new ArrayList<>();

    private Path screenshotPath = Path.of(System.getProperty("user.home") +
            fileSeparator + "fireshot" + fileSeparator + "screenshots");
    private String imageType = "png";

    public FileService(ResourceManager... resourceManager) {
        this.resourceManagers.addAll(List.of(resourceManager));
    }

    public void saveImage(BufferedImage image, String fileName) throws IOException {
        if (!Files.exists(this.screenshotPath)) {
            Files.createDirectories(screenshotPath);
        }
        File file = new File(this.screenshotPath.toString() + fileSeparator + fileName + "." + this.imageType);
        ImageIO.write(image, this.imageType, file);
    }

    public void loadResources() {
        this.resources.clear();
        File file = new File(resourcePath);
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
        this.requiredFiles.forEach(var -> resources.add(this.getAndWriteImage(var)));
    }

    private void invokeResourceManager() {
        this.checkRequiredFiles();
        this.resourceManagers.forEach(var -> var.applyResources(this.resources));
    }

    private void checkRequiredFiles() {
        this.requiredFiles.stream()
                .filter(var -> this.resources.stream().noneMatch(file -> file.getName().equals(var)))
                .forEach(var -> this.resources.add(this.getAndWriteImage(var)));
    }

    private File getAndWriteImage(String imageName) {
        RequestService requestService = Fireshot.getInstance().getRequestService();
        Futures.addCallback(requestService.requestImage(imageName), new FutureCallback<>() {
            @Override
            public void onSuccess(@Nullable Image image) {
                Objects.requireNonNull(image);
                try {
                    ImageIO.write((RenderedImage) image, "png",
                            new File(resourcePath + imageName));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                // TODO warning - could not load all resources
                System.out.println("Could not load all resources");
            }
        }, Fireshot.getInstance().getExecutorService());
        return new File(resourcePath + imageName + ".png");
    }

    @Override
    public void applyConfig(Config config) {
        FileConfig fileConfig = config.getFileConfig();
        this.screenshotPath = Path.of(fileConfig.getImageLocation());
        this.imageType = fileConfig.getImageType();
    }
}
