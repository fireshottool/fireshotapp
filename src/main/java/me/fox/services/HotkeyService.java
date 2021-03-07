package me.fox.services;

import dev.lukasl.jwinkey.UserInputInterceptor;
import dev.lukasl.jwinkey.listener.UserInputEvent;
import lombok.Getter;
import lombok.Setter;
import me.fox.components.ConfigManager;
import me.fox.components.Hotkey;
import me.fox.config.Config;
import me.fox.config.HotkeyConfig;
import me.fox.listeners.keyboard.HotkeyListener;
import me.fox.ui.components.toolbox.ToolboxComponent;
import me.fox.ui.panels.toolbox.ext.ScreenshotToolbox;

import java.util.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.10.2020)
 */

@Getter
@Setter
public class HotkeyService implements ConfigManager {

    private final Map<String, Runnable> hotkeyMap = new HashMap<>();

    private final HotkeyListener hotkeyListener = new HotkeyListener(this);
    private final List<Integer> pressedKeys = new ArrayList<>();
    private final List<Hotkey> hotkeys = new ArrayList<>();
    private final UserInputInterceptor userInputInterceptor;
    private final ScreenshotService screenshotService;
    private final ToolboxComponent drawComponent;
    private final ScreenService screenService;
    private final DrawService drawService;

    private boolean changingHotkey, listening;


    /**
     * Constructor for {@link HotkeyService}
     * Registers the {@link HotkeyService#hotkeyListener}.
     */
    public HotkeyService(ScreenshotService screenshotService, DrawService drawService, ScreenService screenService) {
        this.userInputInterceptor = new UserInputInterceptor()
                .registerAllVirtualKeys()
                .addListeners(this.hotkeyListener);
        this.screenshotService = screenshotService;
        this.screenService = screenService;
        this.drawService = drawService;
        this.drawComponent = ((ScreenshotToolbox) this.screenService.getScreenshotToolbox()).getDrawComponent();

        this.registerHotkeys();
        this.userInputInterceptor.start();
    }

    /**
     * Invokes a {@link HotkeyService#hotkeyMap} method if the {@link Hotkey}
     * is present and {@link Hotkey#canInvoke(UserInputEvent, List)}.
     *
     * @param event for the {@link Hotkey#canInvoke(UserInputEvent, List)} method
     */
    public void invokeIfPresent(UserInputEvent event) {
        Optional<Hotkey> optionalHotkey = this.hotkeys
                .stream().filter(var -> var.canInvoke(event, this.getPressedKeys()) &&
                        this.hotkeyMap.containsKey(var.getName())).findFirst();
        optionalHotkey.ifPresent(var -> this.hotkeyMap.get(var.getName()).run());
    }

    /**
     * Registers a key if pressed to {@link HotkeyService#pressedKeys}.
     *
     * @param keyCode key to register
     */
    public void registerKey(int keyCode) {
        if (!this.pressedKeys.contains(keyCode)) {
            this.pressedKeys.add(keyCode);
        }
    }

    /**
     * Unregisters a key if released from {@link HotkeyService#pressedKeys}.
     *
     * @param keyCode key to unregister
     */
    public void unregisterKey(int keyCode) {
        if (this.pressedKeys.contains(keyCode)) {
            this.pressedKeys.remove((Integer) keyCode);
        }
    }

    private void registerHotkeys() {
        this.hotkeyMap.put("screenshot", this::screenshot);
        this.hotkeyMap.put("escape", this::escape);
        this.hotkeyMap.put("draw", this::draw);
        this.hotkeyMap.put("redo", this::redo);
        this.hotkeyMap.put("undo", this::undo);
        this.hotkeyMap.put("confirm", this::confirm);
        this.hotkeyMap.put("zoom", this::zoom);
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
        if (this.screenService.getDrawToolbox().isVisible()) {
            this.screenService.getDrawToolbox().hideSelf();
        } else {
            this.screenService.getDrawToolbox().showSelf();
        }
        this.drawComponent.select(null);
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
        this.screenService.hideAndConfirm(false, false);
    }

    private void zoom() {
        if (!this.screenService.getScreenshotFrame().isVisible()) return;
        this.screenshotService.setZoom(!this.getScreenshotService().isZoom());
    }

    @Override
    public void applyConfig(Config config) {
        HotkeyConfig hotkeyConfig = config.getHotkeyConfig();
        this.hotkeys.addAll(hotkeyConfig.getHotkeys());
    }
}
