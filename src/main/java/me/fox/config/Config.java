package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class Config {
    public static final Config DEFAULT_CONFIG = new Config();

    private final FileConfig fileConfig = new FileConfig();
    private final DrawConfig drawConfig = new DrawConfig();
    private final HotkeyConfig hotkeyConfig = new HotkeyConfig();
    private final UpdateConfig updateConfig = new UpdateConfig();
    private final RequestConfig requestConfig = new RequestConfig();
    private final ScreenshotConfig screenshotConfig = new ScreenshotConfig();
}
