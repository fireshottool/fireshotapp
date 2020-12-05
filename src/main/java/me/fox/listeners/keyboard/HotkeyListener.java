package me.fox.listeners.keyboard;

import lc.kra.system.keyboard.event.GlobalKeyEvent;
import me.fox.adapter.KeyboardListenerAdapter;
import me.fox.services.HotkeyService;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

public class HotkeyListener extends KeyboardListenerAdapter {

    private final HotkeyService hotkeyService;

    /**
     * Constructor for {@link HotkeyService}
     *
     * @param hotkeyService to set {@link HotkeyListener#hotkeyService}
     */
    public HotkeyListener(HotkeyService hotkeyService) {
        this.hotkeyService = hotkeyService;
    }

    @Override
    public void keyPressed(GlobalKeyEvent event) {
        if (event.getVirtualKeyCode() == 255) return;
        this.hotkeyService.registerKey(event.getVirtualKeyCode());
        if (this.hotkeyService.isChangingHotkey()) return;
        this.hotkeyService.invokeIfPresent(event);
    }

    @Override
    public void keyReleased(GlobalKeyEvent event) {
        if (this.hotkeyService.isChangingHotkey()) return;
        if (event.getVirtualKeyCode() == 255) return;
        this.hotkeyService.unregisterKey(event.getVirtualKeyCode());
    }
}
