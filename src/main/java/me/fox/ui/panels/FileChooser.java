package me.fox.ui.panels;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * @author (Ausgefuchster)
 * @version (~ 11.11.2020)
 */

public class FileChooser extends JFileChooser {

    public FileChooser(File file) {
        this.addActionListener(this::actionPerformed);
        this.setSelectedFile(file);
        this.setCurrentDirectory(file);
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    private void actionPerformed(ActionEvent event) {

    }
}
