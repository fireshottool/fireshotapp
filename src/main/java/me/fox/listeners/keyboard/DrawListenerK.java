package me.fox.listeners.keyboard;

import lc.kra.system.keyboard.event.GlobalKeyEvent;
import me.fox.adapter.KeyboardListenerAdapter;
import me.fox.services.DrawService;

/**
 * @author (Ausgefuchster)
 * @version (~ 01.11.2020)
 */

public class DrawListenerK extends KeyboardListenerAdapter {

    private final DrawService parent;

    public DrawListenerK(DrawService parent) {
        this.parent = parent;
    }

    @Override
    public void keyPressed(GlobalKeyEvent event) {
        if (this.parent.isDraw() && event.getVirtualKeyCode() == 16) {
            this.parent.setLine(true);
        }
    }

    @Override
    public void keyReleased(GlobalKeyEvent event) {
        if (this.parent.isDraw() && event.getVirtualKeyCode() == 16) {
            this.parent.setLine(false);
        }
    }
}
