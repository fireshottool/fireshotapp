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
        System.out.println(event.getVirtualKeyCode());
        this.hotkeyService.registerKey(event.getVirtualKeyCode());
        this.hotkeyService.invokeIfPresent(event);
    }

    @Override
    public void keyReleased(GlobalKeyEvent event) {
        this.hotkeyService.unregisterKey(event.getVirtualKeyCode());
    }
}
