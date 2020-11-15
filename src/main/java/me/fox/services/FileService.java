package me.fox.services;

import lombok.Setter;
import me.fox.config.FileConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Setter
public class FileService {
    private final String fileSeparator = System.getProperty("file.separator");

    private Path screenshotPath = Path.of(System.getProperty("user.home") + fileSeparator + "fireshot" + fileSeparator + "screenshots");
    private String imageType = "png";

    public void saveImage(BufferedImage image, String fileName) throws IOException {
        if (!Files.exists(this.screenshotPath)) {
            Files.createDirectories(screenshotPath);
        }
        File file = new File(this.screenshotPath.toString() + fileSeparator + fileName + "." + this.imageType);
        ImageIO.write(image, this.imageType, file);
    }

    public void applyConfig(FileConfig fileConfig) {
        this.screenshotPath = Path.of(fileConfig.getImageLocation());
        this.imageType = fileConfig.getImageType();
    }
}
