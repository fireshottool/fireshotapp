package me.fox.components;

import me.fox.config.Config;

/**
 * @author (Ausgefuchster)
 * @version (~ 19.11.2020)
 */

public interface ConfigManager {

    /**
     * Load the values from the config
     *
     * @param config to load the values from
     */
    void applyConfig(Config config);
}
