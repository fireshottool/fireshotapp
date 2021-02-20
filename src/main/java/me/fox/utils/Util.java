package me.fox.utils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */
public class Util {

    /**
     * Check whether the {@param cursorType} is valid or not
     *
     * @param cursorType to check
     * @return if the specified cursor type is invalid
     */
    public static boolean isValidCursor(int cursorType) {
        try {
            Cursor.getPredefinedCursor(cursorType);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static String generateRandomString(int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'

        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static void googleSearch(String imageUrl) {
        System.out.println("google search");
        try {
            Desktop.getDesktop().browse(new URI("https://www.google.com/searchbyimage?image_url=" + imageUrl));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
