package me.fox.enums;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 09.01.2021)
 */

public enum ColorPalette {
    DARK_BLUE_200(new Color(5, 5, 25, 200)),
    DARK_BLUE_170(new Color(5, 5, 25, 170)),
    DARK_BLUE_LIGHTER_200(new Color(45, 45, 65, 200)),
    DARK_BLUE_LIGHTER_170(new Color(45, 45, 65, 170));

    private final Color color;

    ColorPalette(Color color) {
        this.color = color;
    }

    public Color get() {
        return this.color;
    }
}
