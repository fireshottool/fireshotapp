package me.fox.ui.frames;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.components.ConfigManager;
import me.fox.config.Config;
import me.fox.services.JsonService;
import me.fox.ui.panels.PanelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author (Ausgefuchster)
 * @version (~ 11.11.2020)
 */

@Getter
public class SettingsFrame extends JFrame implements ConfigManager {

    private final PanelManager panelManager = new PanelManager(this);
    private final JButton okButton, cancelButton, applyButton;

    public SettingsFrame() {
        super("Fireshot - Settings");

        this.setContentPane(this.panelManager);
        this.okButton = new JButton("Ok");
        this.cancelButton = new JButton("Cancel");
        this.applyButton = new JButton("Apply");
        this.setupButtons();
        this.setup();
    }

    private void setup() {
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setFocusable(true);
        this.setVisible(false);
        this.setLayout(null);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private void setupButtons() {
        this.okButton.setLocation(490, 515);
        this.okButton.setSize(80, 35);

        this.cancelButton.setLocation(590, 515);
        this.cancelButton.setSize(80, 35);
        this.applyButton.setLocation(690, 515);
        this.applyButton.setSize(80, 35);

        this.add(this.okButton);
        this.add(this.cancelButton);
        this.add(this.applyButton);

        this.okButton.addActionListener(this::okPerformed);
        this.cancelButton.addActionListener(this::cancelPerformed);
        this.applyButton.addActionListener(this::applyPerformed);
    }

    private void okPerformed(ActionEvent event) {
        JsonService jsonService = Fireshot.getInstance().getJsonService();
        jsonService.saveAndApply();
        this.setVisible(false);
    }

    private void cancelPerformed(ActionEvent event) {
        this.setVisible(false);
        JsonService jsonService = Fireshot.getInstance().getJsonService();
        jsonService.read();
    }

    private void applyPerformed(ActionEvent event) {
        JsonService jsonService = Fireshot.getInstance().getJsonService();
        jsonService.saveAndApply();
    }

    @Override
    public void applyConfig(Config config) {
        this.panelManager.applyConfig(config);
    }
}
