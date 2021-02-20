package me.fox.listeners.mouse;

import me.fox.Fireshotapp;
import me.fox.ui.components.settings.ext.HotkeyComponent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */

public class HotkeyComponentListener extends MouseAdapter {

    private final HotkeyComponent hotkeyComponent;

    /**
     * Constructor for {@link HotkeyComponentListener}
     *
     * @param hotkeyComponent to set {@link HotkeyComponentListener#hotkeyComponent}
     */
    public HotkeyComponentListener(HotkeyComponent hotkeyComponent) {
        this.hotkeyComponent = hotkeyComponent;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        if (hotkeyComponent.isInside()) {
            this.hotkeyComponent.getHotkeyLabel().setForeground(Color.GRAY);
            Fireshotapp.getInstance().getHotkeyService().setChangingHotkey(true);
        }
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        this.hotkeyComponent.setInside(true);
    }

    @Override
    public void mouseExited(MouseEvent event) {
        this.hotkeyComponent.getHotkeyLabel().setForeground(Color.BLACK);
        this.hotkeyComponent.setInside(false);
        Fireshotapp.getInstance().getHotkeyService().setChangingHotkey(false);
    }
}