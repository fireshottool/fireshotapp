package me.fox.ui.components;

import lombok.Getter;
import lombok.Setter;
import me.fox.adapter.Rectangle;

import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.10.2020)
 */

@Getter
@Setter
public class ScalePoint extends Rectangle {

    private final int direction;

    /**
     * Constructor for {@link ScalePoint}
     *
     * @param direction that the {@link ScalePoint} represents
     */
    public ScalePoint(int direction) {
        this.direction = direction;
        this.setSize(10, 10);
    }

    public void updateLocation(Rectangle rect) {
        switch (direction) {
            case Cursor.SW_RESIZE_CURSOR:
                setLocation(rect.x - 5, rect.y + rect.height - 5);
                break;
            case Cursor.SE_RESIZE_CURSOR:
                setLocation(rect.x + rect.width - 5, rect.y + rect.height - 5);
                break;
            case Cursor.NW_RESIZE_CURSOR:
                setLocation(rect.x - 5, rect.y - 5);
                break;
            case Cursor.NE_RESIZE_CURSOR:
                setLocation(rect.x + rect.width - 5, rect.y - 5);
                break;
            case Cursor.N_RESIZE_CURSOR:
                setLocation(rect.x + rect.width / 2 - 5, rect.y - 5);
                break;
            case Cursor.S_RESIZE_CURSOR:
                setLocation(rect.x + rect.width / 2 - 5, rect.y + rect.height - 5);
                break;
            case Cursor.W_RESIZE_CURSOR:
                setLocation(rect.x - 5, rect.y + rect.height / 2 - 5);
                break;
            case Cursor.E_RESIZE_CURSOR:
                setLocation(rect.x + rect.width - 5, rect.y + rect.height / 2 - 5);
                break;
        }
    }
}
