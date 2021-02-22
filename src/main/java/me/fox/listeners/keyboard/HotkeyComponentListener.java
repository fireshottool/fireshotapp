package me.fox.listeners.keyboard;

import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lombok.AllArgsConstructor;
import me.fox.Fireshotapp;
import me.fox.ui.components.settings.ext.HotkeyComponent;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */
@AllArgsConstructor
public class HotkeyComponentListener extends GlobalKeyAdapter {

    private final HotkeyComponent hotkeyComponent;
    @Override
    public void keyPressed(GlobalKeyEvent event) {
        if (this.hotkeyComponent.isInside() && Fireshotapp.getInstance().getHotkeyService().isChangingHotkey()) {
            if (event.getVirtualKeyCode() == 91
                    || event.getVirtualKeyCode() == 17
                    || event.getVirtualKeyCode() == 16
                    || event.getVirtualKeyCode() == 18) return;
            hotkeyComponent.updateHotkey(event);
        }
    }
}
