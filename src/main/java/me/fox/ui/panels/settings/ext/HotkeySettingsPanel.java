package me.fox.ui.panels.settings.ext;

import lombok.Getter;
import me.fox.components.Hotkey;
import me.fox.config.Config;
import me.fox.config.HotkeyConfig;
import me.fox.ui.components.settings.ext.HotkeyComponent;
import me.fox.ui.panels.PanelManager;
import me.fox.ui.panels.settings.SettingsPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author (Ausgefuchster)
 * @version (~ 29.11.2020)
 */

@Getter
public class HotkeySettingsPanel extends SettingsPanel {

    private final JScrollPane jScrollPane;

    public HotkeySettingsPanel(PanelManager panelManager) {
        super(panelManager);
        this.setPreferredSize(new Dimension(561, 1000));
        jScrollPane = new JScrollPane(this,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setSize(580, 500);
        jScrollPane.setLocation(this.getLocation());
        jScrollPane.setVisible(false);
        jScrollPane.setWheelScrollingEnabled(true);
        jScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.setAutoscrolls(true);
    }

    private void applyHotkeySettings(List<Hotkey> hotkeyList) {
        AtomicInteger count = new AtomicInteger(10);
        hotkeyList.forEach(var -> {
            this.add(new HotkeyComponent(var, new Point(10, count.get())));
            count.set(count.get() + 80);
        });
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (jScrollPane == null) return;
        this.jScrollPane.setVisible(aFlag);
    }

    @Override
    public void applyConfig(Config config) {
        HotkeyConfig hotkeyConfig = config.getHotkeyConfig();
        this.removeAll();
        this.applyHotkeySettings(hotkeyConfig.getHotkeys());
    }
}
