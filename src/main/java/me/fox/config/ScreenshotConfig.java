package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class ScreenshotConfig {

    private String zoomMiddleRectColor = "#ffffff";
    private String zoomBorderColor = "#ffffff";
    private String zoomCrossColor = "#0019ff";
    private String zoomGridColor = "#000000";
    private String dimColor = "#000000";
    private boolean zoomMiddleRect = true;
    private boolean askForUpload = true;
    private boolean zoomCross = true;
    private boolean localSave = true;
    private boolean zoomGrid = true;
    private boolean upload = false;
    private boolean zoom = true;
}
