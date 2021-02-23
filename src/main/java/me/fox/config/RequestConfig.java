package me.fox.config;

import lombok.Data;

/**
 * @author (Ausgefuchster)
 * @version (~ 08.12.2020)
 */

@Data
public class RequestConfig {
    private String uploadURL = "https://fireshotapp.eu/storage/upload/";
    private String imageURL = "https://fireshotapp.eu/storage/media/";
    private String imageDetectionURL = "https://fireshotapp.eu/tesseract/ocr/";
}
