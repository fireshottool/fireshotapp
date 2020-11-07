package me.fox.services;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lombok.Getter;
import lombok.Setter;
import me.fox.components.Hotkey;
import me.fox.components.HotkeyFunc;
import me.fox.listeners.keyboard.HotkeyListener;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.panels.ext.ScreenshotToolbox;

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
            Map.entry("screenshot", this::screenshot),
            Map.entry("escape", this::escape),
            Map.entry("draw", this::draw),
            Map.entry("redo", this::redo),
            Map.entry("undo", this::undo),
            Map.entry("confirm", this::confirm)
    );
    private final DrawService drawService;
    private final ScreenService screenService;
    private final ScreenshotService screenshotService;
    private final HotkeyListener hotkeyListener = new HotkeyListener(this);
    private final List<Integer> pressedKeys = new ArrayList<>();
    private final List<Hotkey> hotkeys = new ArrayList<>();
    private final ToolboxComponent drawComponent;
    private final GlobalKeyboardHook globalKeyboardHook;


    /**
     * Constructor for {@link HotkeyService}
     * registers the {@link HotkeyService#hotkeyListener}
     */
    public HotkeyService(ScreenshotService screenshotService, DrawService drawService, ScreenService screenService) {
        globalKeyboardHook = new GlobalKeyboardHook(true);
        globalKeyboardHook.addKeyListener(this.hotkeyListener);
        globalKeyboardHook.addKeyListener(drawService.getDrawListenerK());

        this.screenshotService = screenshotService;
        this.screenService = screenService;
        this.drawService = drawService;
        this.drawComponent = ((ScreenshotToolbox) this.screenService.getScreenshotToolbox()).getDraw();
    }

    /**
     * Invoke a {@link HotkeyService#hotkeyMap} method
     * if the {@link Hotkey} is present and {@link Hotkey#isValid(GlobalKeyEvent, HotkeyService)}
     *
     * @param event for the {@link Hotkey#isValid(GlobalKeyEvent, HotkeyService)} method
     */
    public void invokeIfPresent(GlobalKeyEvent event) {
        Optional<Hotkey> optionalHotkey = this.hotkeys
                .stream().filter(var -> var.isValid(event, this) &&
                        hotkeyMap.containsKey(var.getName())).findFirst();
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

    private void screenshot() {
        if (this.screenService.getScreenshotFrame().isVisible()) return;
        this.screenService.show();
    }

    private void escape() {
        if (!this.screenService.getScreenshotFrame().isVisible()) return;
        this.screenService.resetAndHide();
    }

    private void draw() {
        if (!this.screenService.getScreenshotFrame().isVisible()) return;
        this.drawService.setDraw(!this.drawService.isDraw());
        drawComponent.select(null);
    }

    private void redo() {
        if (!this.screenService.getScreenshotFrame().isVisible()) return;
        this.drawService.redoDrawing();
    }

    private void undo() {
        if (!this.screenService.getScreenshotFrame().isVisible()) return;
        this.drawService.undoDrawing();
    }

    private void confirm() {
        if (!this.screenService.getScreenshotFrame().isVisible()) return;
        this.screenService.hideAndConfirm();
    }
}
