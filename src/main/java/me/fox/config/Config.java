package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class Config {
    public static final Config DEFAULT_CONFIG = new Config();

    private FileConfig fileConfig = new FileConfig();
    private DrawConfig drawConfig = new DrawConfig();
    private HotkeyConfig hotkeyConfig = new HotkeyConfig();
    private ScreenshotConfig screenshotConfig = new ScreenshotConfig();
}
