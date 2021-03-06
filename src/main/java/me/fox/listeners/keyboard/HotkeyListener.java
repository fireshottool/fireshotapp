package me.fox.listeners.keyboard;

import dev.lukasl.jwinkey.listener.UserInputEvent;
import dev.lukasl.jwinkey.listener.UserInputListenerAdapter;
import lombok.AllArgsConstructor;
import me.fox.services.HotkeyService;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */
@AllArgsConstructor
public class HotkeyListener extends UserInputListenerAdapter {

    private final HotkeyService hotkeyService;

    @Override
    public void keyPressed(UserInputEvent event) {
        if (event.getKeyCode() == 255) return;
        this.hotkeyService.registerKey(event.getKeyCode());
        if (this.hotkeyService.isChangingHotkey()) return;
        this.hotkeyService.invokeIfPresent(event);
    }

    @Override
    public void keyReleased(UserInputEvent event) {
        if (this.hotkeyService.isChangingHotkey()) return;
        if (event.getKeyCode() == 255) return;
        this.hotkeyService.unregisterKey(event.getKeyCode());
    }
}
