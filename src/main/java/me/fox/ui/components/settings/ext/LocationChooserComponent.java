package me.fox.ui.components.settings.ext;

import com.sun.javafx.application.PlatformImpl;
import javafx.stage.DirectoryChooser;
import lombok.Getter;
import me.fox.Fireshotapp;
import me.fox.services.JsonService;
import me.fox.ui.components.settings.SettingsComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author (Ausgefuchster)
 * @version (~ 01.12.2020)
 */

@Getter
public class LocationChooserComponent extends SettingsComponent {

    private final JButton button;
    private final JLabel label;
    private final String labelText;

    private final DirectoryChooser fileChooser = new DirectoryChooser();

    public LocationChooserComponent(Point location, String label) {
        super(location);

        this.labelText = label;
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

    private void actionPerformed(ActionEvent event) {
        PlatformImpl.startup(() -> {
            File file = this.fileChooser.showDialog(null);
            if (file == null) return;
            JsonService jsonService = Fireshotapp.getInstance().getJsonService();
            String filePath = file.toString();
            jsonService.getConfig().getFileConfig().setImageLocation(filePath);
            this.label.setText(labelText + filePath);
        });
    }
}
