package me.fox.services;

import me.fox.components.ConfigManager;
import me.fox.config.Config;

/**
 * @author (Ausgefuchster)
 * @version (~ 09.03.2021)
 */

public interface Service extends ConfigManager {

    @Override
    void applyConfig(Config config);
}
