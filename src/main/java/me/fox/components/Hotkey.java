package me.fox.components;

import dev.lukasl.jwinkey.listener.UserInputEvent;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

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
     * @param event to get the pressed keyCode
     * @param keys  the keys to check
     * @return whether the {@link Hotkey} can be invoked
     */
    public boolean canInvoke(UserInputEvent event, List<Integer> keys) {
        if (this.hotkey != event.getKeyCode()) return false;
        if (this.requiredKeys == null) return true;
        return this.requiredKeysPressed(keys);
    }

    /**
     * Checks whether the {@link Hotkey#requiredKeys} are pressed.
     *
     * @param keys the keys to check
     * @return whether the {@link Hotkey#requiredKeys} are pressed
     */
    private boolean requiredKeysPressed(List<Integer> keys) {
        return keys.containsAll(Arrays.asList(this.requiredKeys));
    }
}
