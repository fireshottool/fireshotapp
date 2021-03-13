package me.fox.ui.components.settings.ext;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.Hotkey;
import me.fox.listeners.keyboard.HotkeyComponentListener;
import me.fox.services.HotkeyService;
import me.fox.services.ScreenService;
import me.fox.ui.components.settings.SettingsComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */

@Getter
@Setter
public class HotkeyComponent extends SettingsComponent {

    private final JLabel textLabel, hotkeyLabel;
    private final Hotkey hotkey;
    private boolean inside;

    public HotkeyComponent(Hotkey hotkey, Point location) {
        super(location);
        this.hotkey = hotkey;
        this.textLabel = new JLabel(hotkey.getDisplayName());
        this.add(textLabel);
        this.textLabel.setLocation(10, 10);
        this.textLabel.setSize(180, 60);

        this.hotkeyLabel = new JLabel(this.parse(hotkey));
        this.add(hotkeyLabel);
        this.hotkeyLabel.setLocation(190, 10);
        this.hotkeyLabel.setSize(180, 60);
        this.hotkeyLabel.setFont(new Font(null, Font.ITALIC, 12));
        this.addMouseListener(new me.fox.listeners.mouse.HotkeyComponentListener(this));
        Fireshotapp.getInstance().use(ScreenService.class).getScreenshotFrame().addKeyListener(new HotkeyComponentListener(this));
    }

    public void updateHotkey(KeyEvent event) {
        this.hotkey.setHotkey(event.getKeyCode());
        HotkeyService hotkeyService = Fireshotapp.getInstance().use(HotkeyService.class);
        List<Integer> pressedKeys = new ArrayList<>(hotkeyService.getPressedKeys());
        pressedKeys.remove((Integer) event.getKeyCode());
        this.hotkey.setRequiredKeys(pressedKeys.toArray(new Integer[0]));
        this.hotkeyLabel.setText(this.parse(this.hotkey));
    }

    private String parse(Hotkey hotkey) {
        return this.requiredKeysToString(hotkey)
                + KeyEvent.getKeyText(hotkey.getHotkey());
    }

    private String requiredKeysToString(Hotkey hotkey) {
        if (hotkey.getRequiredKeys() == null) return "";

        StringBuilder stringBuilder = new StringBuilder();

        Arrays.stream(hotkey.getRequiredKeys()).forEach(var -> {
            stringBuilder.append(KeyEvent.getKeyText(var));
            stringBuilder.append(" + ");
        });

        return stringBuilder.toString();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, 0, this.getWidth(), 0);
    }
}
