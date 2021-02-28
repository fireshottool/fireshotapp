package me.fox.listeners.keyboard;

import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lombok.AllArgsConstructor;
import me.fox.services.HotkeyService;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */
@AllArgsConstructor
public class HotkeyListener extends GlobalKeyAdapter {

    private final HotkeyService hotkeyService;

    @Override
    public void keyPressed(GlobalKeyEvent event) {
        if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_SNAPSHOT) return;
        this.hotkeyService.registerKey(event.getVirtualKeyCode());
        if (this.hotkeyService.isChangingHotkey()) return;
        this.hotkeyService.invokeIfPresent(event);
    }

    @Override
    public void keyReleased(GlobalKeyEvent event) {
        if (this.hotkeyService.isChangingHotkey()) return;
        if (event.getVirtualKeyCode() == GlobalKeyEvent.VK_SNAPSHOT) return;
        this.hotkeyService.unregisterKey(event.getVirtualKeyCode());
    }
}
