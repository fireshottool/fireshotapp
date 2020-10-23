package me.fox.services;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lombok.Getter;
import lombok.Setter;
import me.fox.components.Hotkey;
import me.fox.components.HotkeyFunc;
import me.fox.listeners.keyboard.HotkeyListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
@Setter
public class HotkeyService {

    private final Map<String, HotkeyFunc> hotkeyMap = Map.ofEntries(

    );

    private final List<Integer> pressedKeys = new ArrayList<>();
    private final List<Hotkey> hotkeys = new ArrayList<>();
    private final HotkeyListener hotkeyListener = new HotkeyListener(this);

    /**
     * Constructor for {@link HotkeyService}
     * registers the {@link HotkeyService#hotkeyListener}
     */
    public HotkeyService() {
        GlobalKeyboardHook globalKeyboardHook = new GlobalKeyboardHook(true);
        globalKeyboardHook.addKeyListener(this.hotkeyListener);
    }

    /**
     * Invoke a {@link HotkeyService#hotkeyMap} method
     * if the {@link Hotkey} is present and {@link Hotkey#isValid(GlobalKeyEvent, HotkeyService)}
     *
     * @param event for the {@link Hotkey#isValid(GlobalKeyEvent, HotkeyService)} method
     */
    public void invokeIfPresent(GlobalKeyEvent event) {
        Optional<Hotkey> optionalHotkey = this.hotkeys
                .stream().filter(var -> var.isValid(event, this)).findFirst();
        optionalHotkey.ifPresent(var -> hotkeyMap.get(var.getName()).invoke());
    }

    /**
     * Register a key if pressed to {@link HotkeyService#pressedKeys}
     *
     * @param keyCode key to register
     */
    public void registerKey(int keyCode) {
        if (!pressedKeys.contains(keyCode)) {
            pressedKeys.add(keyCode);
        }
    }

    /**
     * Unregister a key if released from {@link HotkeyService#pressedKeys}
     *
     * @param keyCode key to unregister
     */
    public void unregisterKey(int keyCode) {
        if (pressedKeys.contains(keyCode)) {
            pressedKeys.remove((Integer) keyCode);
        }
    }
}
