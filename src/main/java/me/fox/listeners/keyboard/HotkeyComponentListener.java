package me.fox.listeners.keyboard;

import lc.kra.system.keyboard.event.GlobalKeyEvent;
import me.fox.Fireshot;
import me.fox.adapter.KeyboardListenerAdapter;
import me.fox.ui.components.settings.ext.HotkeyComponent;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */

public class HotkeyComponentListener extends KeyboardListenerAdapter {

    private final HotkeyComponent hotkeyComponent;

    public HotkeyComponentListener(HotkeyComponent hotkeyComponent) {
        this.hotkeyComponent = hotkeyComponent;
    }

    @Override
    public void keyPressed(GlobalKeyEvent event) {
        if (this.hotkeyComponent.isInside() && Fireshot.getInstance().getHotkeyService().isChangingHotkey()) {
            if (event.getVirtualKeyCode() == 91
                    || event.getVirtualKeyCode() == 17
                    || event.getVirtualKeyCode() == 16
                    || event.getVirtualKeyCode() == 18) return;
            hotkeyComponent.updateHotkey(event);
        }
    }
}
