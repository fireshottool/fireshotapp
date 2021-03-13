package me.fox.listeners.keyboard;

import lombok.AllArgsConstructor;
import me.fox.services.HotkeyService;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */
@AllArgsConstructor
public class HotkeyListener extends KeyAdapter {

    private final HotkeyService hotkeyService;

    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == 255) return;
        this.hotkeyService.registerKey(event.getKeyCode());
        if (this.hotkeyService.isChangingHotkey()) return;
        this.hotkeyService.invokeIfPresent(event);
    }

    @Override
    public void keyReleased(KeyEvent event) {
        if (this.hotkeyService.isChangingHotkey()) return;
        if (event.getKeyCode() == 255) return;
        this.hotkeyService.unregisterKey(event.getKeyCode());
    }
}
