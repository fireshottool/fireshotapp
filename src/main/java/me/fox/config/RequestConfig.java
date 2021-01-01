package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 08.12.2020)
 */

@Data
public class RequestConfig {

    private String uploadURL = "https://firedata.eu:8443/upload", imageURL = "https://firedata.eu:8443/image",
            imageDetectionURL = "https://firedata.eu:8433/imageDetectiopn";
}
