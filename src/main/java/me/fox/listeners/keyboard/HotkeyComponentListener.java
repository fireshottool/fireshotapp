package me.fox.listeners.keyboard;

import lombok.AllArgsConstructor;
import me.fox.Fireshotapp;
import me.fox.services.HotkeyService;
import me.fox.ui.components.settings.ext.HotkeyComponent;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */
@AllArgsConstructor
public class HotkeyComponentListener extends KeyAdapter {

    private final HotkeyComponent hotkeyComponent;

    @Override
    public void keyPressed(KeyEvent event) {
        if (this.hotkeyComponent.isInside() && Fireshotapp.getInstance().use(HotkeyService.class).isChangingHotkey()) {
            if (event.getKeyCode() == KeyEvent.VK_WINDOWS
                    || event.getKeyCode() == KeyEvent.VK_SHIFT
                    || event.getKeyCode() == KeyEvent.VK_CONTROL
                    || event.getKeyCode() == KeyEvent.VK_ALT)
            return;
            this.hotkeyComponent.updateHotkey(event);
        }
    }
}
