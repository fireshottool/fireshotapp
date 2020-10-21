package me.fox.services;

import lombok.Getter;
import lombok.Setter;
import me.fox.ui.components.ScalableRectangle;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
@Setter
public class ScreenshotService {

    private BufferedImage image;
    private ScalableRectangle selectionRectangle;

    public void createScreenshot() {
        try {
            this.image = new Robot().createScreenCapture(new Rectangle(10, 10, 10, 10));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
