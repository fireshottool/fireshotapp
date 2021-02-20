package me.fox.ui.frames;

import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;

/**
 * @author (Ausgefuchster)
 * @version (~ 16.02.2021)
 */

public class OptionCheckboxFrame extends JPanel {

    private final JCheckBox checkBox;

    public OptionCheckboxFrame(String message) {
        this.checkBox = new JCheckBox("Don't ask me again");
        JLabel label = new JLabel(message);
        this.add(label);
        this.add(checkBox);
    }

    /**
     * Shows a dialog with the options Yes, No and Cancel
     * And it also contains a checkbox with "Don't ask me again"
     *
     * @param parent  determines the Frame in which the dialog is displayed,
     *                also see {@link JOptionPane#showConfirmDialog(Component, Object)}
     * @param message Message displayed for the user
     * @return a {@link Pair} with the result of the 3 options and whether the checkbox is selected or not
     */
    public static Pair<Integer, Boolean> showDialog(Component parent, String message, String title) {
        int result;
        OptionCheckboxFrame panel = new OptionCheckboxFrame(message);
        result = JOptionPane.showConfirmDialog(parent, panel, title, JOptionPane.YES_NO_CANCEL_OPTION);
        return new Pair<>(result, panel.checkBox.isSelected());
    }
}
