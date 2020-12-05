package me.fox.ui.components.settings.ext;

import lombok.Getter;
import me.fox.Fireshot;
import me.fox.ui.components.settings.FoldEdgeLabel;
import me.fox.ui.components.settings.SettingsComponent;
import me.fox.ui.frames.ColorPickerDialog;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author (Ausgefuchster)
 * @version (~ 21.11.2020)
 */


@Getter
public class ColorComponent extends SettingsComponent {

    private final FoldEdgeLabel foldEdgeLabel;
    private final JLabel label;
    private final JButton button;
    private final ColorPickerDialog colorPickerDialog;

    public ColorComponent(String label, Point location, ActionListener colorChangedListener, JFrame frame) {
        super(location);

        this.foldEdgeLabel = new FoldEdgeLabel(Color.red.toString());
        this.label = new JLabel(label);
        this.button = new JButton("Choose color");
        this.colorPickerDialog = new ColorPickerDialog(frame, this::colorChanged);

        this.getColorPickerDialog().getOk().addActionListener(colorChangedListener);
        this.getColorPickerDialog().getCancel().addActionListener(this::cancelPerformed);

        this.setupLabel();
        this.setupButton();
    }

    private void setupLabel() {
        this.label.setLocation(5, 10);
        this.label.setSize(100, 60);
        this.add(foldEdgeLabel);
        this.foldEdgeLabel.setLocation(80, 10);
        this.foldEdgeLabel.setSize(100, 60);
        this.foldEdgeLabel.setColor(Color.red);
        this.add(label);
    }

    private void setupButton() {
        this.button.setLocation(200, 30);
        this.button.setSize(100, 30);
        this.add(button);
        this.button.addActionListener(this::actionPerformed);
    }

    private void actionPerformed(ActionEvent event) {
        this.colorPickerDialog.setVisible(true);
    }

    private void cancelPerformed(ActionEvent event) {
        this.colorPickerDialog.setVisible(false);
        this.foldEdgeLabel.setColor(Fireshot.getInstance().getDrawService().getDrawColor());
    }

    private void colorChanged(ChangeEvent event) {
        this.setColor(this.colorPickerDialog.getColorPicker().getColor());
    }

    public Color getColor() {
        return this.foldEdgeLabel.getColor();
    }

    public void setColor(Color color) {
        this.foldEdgeLabel.setColor(color);
        this.colorPickerDialog.setColor(color);
    }
}
