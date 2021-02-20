package me.fox.config;

import com.google.common.collect.Lists;
import lombok.Data;
import me.fox.components.Hotkey;

import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 15.11.2020)
 */

@Data
public class HotkeyConfig {

    private List<Hotkey> hotkeys = Lists.newArrayList(
            new Hotkey("screenshot", "Create screenshot", 44),
            new Hotkey("escape", "Escape", 27),
            new Hotkey("draw", "Switch to draw", 68, 17),
            new Hotkey("undo", "Undo", 90, 17),
            new Hotkey("redo", "Redo", 89, 17),
            new Hotkey("confirm", "Confirm screenshot", 13),
            new Hotkey("zoom", "Activate zoom", 76, 17)
    );
}
