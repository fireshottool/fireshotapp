package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.01.2021)
 */

@Data
public class UpdateConfig {

    private boolean updated = false;
    private boolean update = false;
    private boolean askForUpdate = true;
}
