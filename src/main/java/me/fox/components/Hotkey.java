package me.fox.components;

import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lombok.Getter;
import lombok.Setter;
import me.fox.services.HotkeyService;

import java.util.Arrays;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
@Setter
public class Hotkey {

    private String name, displayName;
    private int hotkey;
    private Integer[] requiredKeys;

    public Hotkey(String name, String displayName, int hotkey, Integer... requiredKeys) {
        this.name = name;
        this.displayName = displayName;
        this.hotkey = hotkey;
        this.requiredKeys = requiredKeys;
    }

    /**
     * Checks if the {@link Hotkey} fits and can be invoked.
     *
     * @param event         to get the pressed keyCode
     * @param hotkeyService to check if the {@link Hotkey#requiredKeys} are pressed
     * @return whether the {@link Hotkey} can be invoked or not
     */
    public boolean canInvoke(GlobalKeyEvent event, HotkeyService hotkeyService) {
        if (this.hotkey != event.getVirtualKeyCode()) return false;
        if (this.requiredKeys == null) return true;
        return this.requiredKeysPressed(hotkeyService);
    }

    /**
     * Checks whether the {@link Hotkey#requiredKeys} are pressed.
     *
     * @param hotkeyService to get the {@link java.util.List}
     *                      of pressed keys {@link HotkeyService#getPressedKeys()}
     * @return whether the {@link Hotkey#requiredKeys} are pressed or not
     */
    private boolean requiredKeysPressed(HotkeyService hotkeyService) {
        return hotkeyService.getPressedKeys().containsAll(Arrays.asList(requiredKeys));
    }
}
