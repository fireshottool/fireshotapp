package me.fox.config;

import lombok.Data;
import me.fox.components.Hotkey;

import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class HotkeyConfig {

    private List<Hotkey> hotkeys = List.of(
            new Hotkey("screenshot", 44),
            new Hotkey("escape", 27),
            new Hotkey("draw", 68, 17),
            new Hotkey("undo", 90, 17),
            new Hotkey("redo", 89, 17),
            new Hotkey("confirm", 13)
    );
}
