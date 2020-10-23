package me.fox.listeners.mouse;

import me.fox.adapter.MouseListenerAdapter;
import me.fox.services.DrawService;

/**
 * @author (Ausgefuchster)
 * @version (~ 23.10.2020)
 */

public class DrawListener extends MouseListenerAdapter {

    private final DrawService parent;

    public DrawListener(DrawService parent) {
        this.parent = parent;
    }


}
