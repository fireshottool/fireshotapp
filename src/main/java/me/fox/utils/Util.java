package me.fox.utils;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

public class Util {

    /**
     * Check whether the {@param cursorType} is valid or not
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
}
