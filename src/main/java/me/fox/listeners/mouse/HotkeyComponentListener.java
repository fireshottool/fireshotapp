package me.fox.listeners.mouse;

import lombok.AllArgsConstructor;
import me.fox.Fireshotapp;
import me.fox.services.HotkeyService;
import me.fox.ui.components.settings.ext.HotkeyComponent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */
@AllArgsConstructor
public class HotkeyComponentListener extends MouseAdapter {

    private final HotkeyComponent hotkeyComponent;

    @Override
    public void mouseClicked(MouseEvent event) {
        if (this.hotkeyComponent.isInside()) {
            this.hotkeyComponent.getHotkeyLabel().setForeground(Color.GRAY);
            Fireshotapp.getInstance().use(HotkeyService.class).setChangingHotkey(true);
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
        Fireshotapp.getInstance().use(HotkeyService.class).setChangingHotkey(false);
    }
}
