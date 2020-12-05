package me.fox.ui.components.settings.ext;

import lombok.Getter;
import lombok.Setter;
import me.fox.ui.components.settings.SettingsComponent;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 19.11.2020)
 */

@Getter
@Setter
public class SpinnerNumberComponent extends SettingsComponent {

    private final JSpinner spinner;
    private final JLabel label;

    public SpinnerNumberComponent(String label, Point location, ChangeListener spinnerChangeListener,
                                  int defaultValue, int step) {
        super(location);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(defaultValue, 0, Integer.MAX_VALUE, step);
        this.spinner = new JSpinner(spinnerModel);
        this.label = new JLabel(label);

        this.setupSpinnerModel(spinnerChangeListener);
        this.setupLabel();
    }

    public SpinnerNumberComponent(String label, Point location, ChangeListener spinnerChangeListener,
                                  int defaultValue, int min, int max, int step) {
        super(location);
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(defaultValue, min, max, step);
        this.spinner = new JSpinner(spinnerModel);
        this.label = new JLabel(label);

        this.setupSpinnerModel(spinnerChangeListener);
        this.setupLabel();
    }

    public int getValue() {
        return (Integer) this.spinner.getValue();
    }

    public void setValue(int value) {
        this.spinner.setValue(value);
    }

    private void setupLabel() {
        this.label.setSize(180, 30);
        this.label.setLocation(5, 10);
        this.add(label);
    }

    private void setupSpinnerModel(ChangeListener changeListener) {
        this.spinner.setSize(100, 30);
        this.spinner.setLocation(200, 10);
        this.spinner.addChangeListener(changeListener);
        this.add(spinner);
    }
}
