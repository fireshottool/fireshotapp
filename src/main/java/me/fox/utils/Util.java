package me.fox.utils;

import java.awt.*;
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

    public static void startBrowser(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();
        try {
            if (os.contains("win")) {

                // this doesn't support showing urls in the form of "page.html#nameLink"
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);

            } else if (os.contains("mac")) {

                rt.exec("open " + url);

            } else if (os.contains("nix") || os.contains("nux")) {

                // Do a best guess on unix until we get a platform independent way
                // Build a list of browsers to try, in this order.
                String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
                        "netscape", "opera", "links", "lynx"};

                // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
                StringBuilder cmd = new StringBuilder();
                for (int i = 0; i < browsers.length; i++)
                    cmd.append(i == 0 ? "" : " || ").append(browsers[i]).append(" \"").append(url).append("\" ");

                rt.exec(new String[]{"sh", "-c", cmd.toString()});

            }
        } catch (Exception ignored) {
        }
    }
}
