package me.fox.ui.components.settings.ext;

import lombok.Getter;
import me.fox.ui.components.settings.SettingsComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * @author (Ausgefuchster)
 * @version (~ 03.12.2020)
 */

@Getter
public class CheckBoxComponent extends SettingsComponent {

    private final JLabel label = new JLabel();
    private final JCheckBox checkBox = new JCheckBox();

    public CheckBoxComponent(Point location, String label, ActionListener actionListener) {
        super(location);

        this.label.setText(label);
        this.label.setLocation(5, 10);
        this.label.setSize(100, 60);
        this.add(this.label);

        this.checkBox.setSize(20, 20);
        this.checkBox.setLocation(110, 30);
        this.add(this.checkBox);
        this.checkBox.addActionListener(actionListener);
    }
}
