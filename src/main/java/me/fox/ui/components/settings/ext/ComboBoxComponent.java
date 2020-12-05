package me.fox.ui.components.settings.ext;

import lombok.Getter;
import me.fox.ui.components.settings.SettingsComponent;

import javax.swing.*;
import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 03.12.2020)
 */

@Getter
public class ComboBoxComponent extends SettingsComponent {

    private final JLabel label = new JLabel();
    private final JComboBox<String> comboBox = new JComboBox<>();

    public ComboBoxComponent(Point location, String label, String... comboBoxOptions) {
        super(location);

        this.label.setText(label);
        this.label.setLocation(5, 10);
        this.label.setSize(100, 60);
        this.add(this.label);

        this.comboBox.setModel(new DefaultComboBoxModel<>(comboBoxOptions));
        this.comboBox.setLocation(110, 25);
        this.comboBox.setSize(60, 30);
        this.add(this.comboBox);
    }
}
