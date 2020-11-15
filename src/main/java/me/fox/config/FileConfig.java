package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class FileConfig {

    private String imageLocation = System.getProperty("user.home") +
            System.getProperty("file.separator") + "fireshot";
    private String imageType = "png";
}
