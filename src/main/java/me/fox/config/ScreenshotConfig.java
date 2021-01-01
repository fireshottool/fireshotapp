package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class ScreenshotConfig {

    private String dimColor = "#000000";
    private String zoomCrossColor = "#0019ff";
    private String zoomRasterColor = "#000000";
    private boolean localSave = false;
    private boolean upload = true;
    private boolean zoom = true;
}
