package me.fox.listeners.keyboard;

import dev.lukasl.jwinkey.VirtualKey;
import dev.lukasl.jwinkey.listener.UserInputEvent;
import dev.lukasl.jwinkey.listener.UserInputListenerAdapter;
import lombok.AllArgsConstructor;
import me.fox.Fireshotapp;
import me.fox.services.HotkeyService;
import me.fox.ui.components.settings.ext.HotkeyComponent;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */
@AllArgsConstructor
public class HotkeyComponentListener extends UserInputListenerAdapter {

    private final HotkeyComponent hotkeyComponent;

    @Override
    public void keyPressed(UserInputEvent event) {
        if (this.hotkeyComponent.isInside() && Fireshotapp.getInstance().use(HotkeyService.class).isChangingHotkey()) {
            if (event.getKeyCode() == VirtualKey.VK_LEFT_WIN.getKeyCode()
                    || event.getKeyCode() == VirtualKey.VK_SHIFT.getKeyCode()
                    || event.getKeyCode() == VirtualKey.VK_CONTROL.getKeyCode()
                    || event.getKeyCode() == VirtualKey.VK_MENU.getKeyCode())
                return;
            this.hotkeyComponent.updateHotkey(event);
        }
    }
}
