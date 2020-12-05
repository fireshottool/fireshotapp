package me.fox.ui.components.settings.ext;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.services.JsonService;
import me.fox.ui.components.settings.SettingsComponent;
import me.fox.ui.panels.FileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

/**
 * @author (Ausgefuchster)
 * @version (~ 01.12.2020)
 */

@Getter
public class LocationChooserComponent extends SettingsComponent {

    private final JButton button;
    private final JLabel label;
    private final String labelText;

    private final FileChooser fileChooser;

    public LocationChooserComponent(Point location, String label) {
        super(location);

        this.labelText = label;
        this.fileChooser = new FileChooser(new File(System.getProperty("user.home")), this::locationChanged);
        this.button = new JButton("Choose Location");
        this.button.setSize(120, 30);
        this.button.setLocation(20, 40);
        this.label = new JLabel(label);
        this.label.setLocation(10, 0);
        this.label.setSize(350, 60);

        this.add(this.button);
        this.add(this.label);
        this.button.addActionListener(this::actionPerformed);
    }

    public void setLocationText(String string) {
        this.label.setText(labelText + string);
    }

    private void locationChanged(ActionEvent event) {
        JsonService jsonService = Fireshot.getInstance().getJsonService();
        Path path = this.fileChooser.getSelectedFile().toPath();
        jsonService.getConfig().getFileConfig().setImageLocation(path.toString());
        this.label.setText(labelText + path.toString());
    }

    private void actionPerformed(ActionEvent event) {
        this.fileChooser.showDialog(this, "Select Folder");
    }
}
