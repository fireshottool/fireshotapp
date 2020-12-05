package me.fox.ui.frames;

import lombok.Getter;
import me.fox.ui.panels.ColorPicker;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 22.11.2020)
 */

@Getter
public class ColorPickerDialog extends JDialog {

    private final ColorPicker colorPicker;
    private final JButton ok, cancel;

    public ColorPickerDialog(JFrame parent, ChangeListener changeListener) {
        super(parent, "Color picker");
        this.colorPicker = new ColorPicker(changeListener);

        this.ok = new JButton("Ok");
        this.cancel = new JButton("Cancel");
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.setup();
    }

    private void setup() {
        this.setVisible(false);
        this.setLayout(null);
        this.setSize(255, 300);
        this.setResizable(false);
        this.setLocationRelativeTo(this);
        this.setAlwaysOnTop(true);
        this.add(this.colorPicker);
        this.colorPicker.setLocation(5, 5);

        this.add(this.ok);
        this.ok.setLocation(40, 220);
        this.ok.setSize(80, 35);

        this.add(this.cancel);
        this.cancel.setLocation(140, 220);
        this.cancel.setSize(80, 35);
    }

    public void setColor(Color color) {
        this.colorPicker.setColor(color);
    }
}
