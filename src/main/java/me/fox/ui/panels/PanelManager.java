package me.fox.ui.panels;

import lombok.Getter;
import lombok.Setter;
import me.fox.components.ConfigManager;
import me.fox.config.Config;
import me.fox.ui.components.settings.SettingsButton;
import me.fox.ui.frames.SettingsFrame;
import me.fox.ui.panels.settings.SettingsPanel;
import me.fox.ui.panels.settings.ext.DrawSettingsPanel;
import me.fox.ui.panels.settings.ext.HotkeySettingsPanel;
import me.fox.ui.panels.settings.ext.ScreenshotSettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author (Ausgefuchster)
 * @version (~ 18.11.2020)
 */

@Getter
@Setter
public class PanelManager extends JComponent implements ConfigManager {

    private final SettingsFrame settingsFrame;
    private final SettingsPanel drawSettingsPanel, screenshotSettingsPanel, hotkeySettingsPanel;
    private final Map<SettingsButton, SettingsPanel> settingsMap = new HashMap<>();

    public PanelManager(SettingsFrame settingsFrame) {
        this.settingsFrame = settingsFrame;

        this.drawSettingsPanel = new DrawSettingsPanel(this);
        this.screenshotSettingsPanel = new ScreenshotSettingsPanel(this);
        this.hotkeySettingsPanel = new HotkeySettingsPanel(this);
        this.add(((HotkeySettingsPanel) this.hotkeySettingsPanel).getJScrollPane());
        this.add(drawSettingsPanel);
        this.add(screenshotSettingsPanel);

        this.setupButtons();
    }

    private void setupButtons() {
        SettingsButton drawButton = new SettingsButton(
                "Draw",
                new Point(2, 2),
                this::actionPerformed
        );
        this.settingsMap.put(drawButton, drawSettingsPanel);
        this.add(drawButton);

        SettingsButton screenshotButton = new SettingsButton(
                "Screenshot",
                new Point(2, 102),
                this::actionPerformed
        );
        this.settingsMap.put(screenshotButton, screenshotSettingsPanel);
        this.add(screenshotButton);

        SettingsButton hotkeyButton = new SettingsButton(
                "Hotkeys",
                new Point(2, 202),
                this::actionPerformed
        );
        this.settingsMap.put(hotkeyButton, hotkeySettingsPanel);
        this.add(hotkeyButton);
    }

    private void actionPerformed(ActionEvent event) {
        this.settingsMap.forEach((key, value) -> value.setVisible(key.equals(event.getSource())));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.DARK_GRAY);
        g.drawLine(0, 505, 800, 505);
        repaint();
    }

    @Override
    public void applyConfig(Config config) {
        this.screenshotSettingsPanel.applyConfig(config);
        this.hotkeySettingsPanel.applyConfig(config);
        this.drawSettingsPanel.applyConfig(config);
    }
}
