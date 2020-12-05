package me.fox.ui.panels.settings;

import lombok.Getter;
import me.fox.components.ConfigManager;
import me.fox.ui.panels.PanelManager;

import javax.swing.*;


/**
 * @author (Ausgefuchster)
 * @version (~ 18.11.2020)
 */


@Getter
public abstract class SettingsPanel extends JPanel implements ConfigManager {

    private final PanelManager panelManager;

    public SettingsPanel(PanelManager panelManager) {
        this.panelManager = panelManager;
        this.setVisible(false);
        this.setLayout(null);
        this.setSize(596, 500);
        this.setLocation(204, 0);
    }
}
