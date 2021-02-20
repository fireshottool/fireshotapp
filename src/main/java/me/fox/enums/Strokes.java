package me.fox.enums;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 09.01.2021)
 */

public enum Strokes {
    WIDTH_ONE_STROKE(new BasicStroke(1)),
    WIDTH_TWO_STROKE(new BasicStroke(2));

    private final Stroke stroke;

    Strokes(Stroke stroke) {
        this.stroke = stroke;
    }

    public Stroke get() {
        return this.stroke;
    }
}
