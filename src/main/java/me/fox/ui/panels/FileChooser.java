package me.fox.ui.panels;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author (Ausgefuchster)
 * @version (~ 11.11.2020)
 */

public class FileChooser extends JFileChooser {

    public FileChooser(File file, ActionListener actionListener) {
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.setCurrentDirectory(file);
        this.setSelectedFile(file);
        this.addActionListener(actionListener);
    }
}
