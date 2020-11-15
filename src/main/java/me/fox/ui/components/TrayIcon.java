package me.fox.ui.components;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;

/**
 * @author (Ausgefuchster)
 * @version (~ 12.11.2020)
 */

@Getter
@Setter
public class TrayIcon extends java.awt.TrayIcon {

    private final PopupMenu popupMenu = new PopupMenu();

    private MenuItem exitItem, settingsItem;
    private CheckboxMenuItem localSaveItem, uploadItem;

    public TrayIcon(BufferedImage image, String tooltip) {
        super(image, tooltip);
        this.setup();
        if (SystemTray.isSupported()) {
            this.setPopupMenu(this.popupMenu);
            try {
                SystemTray.getSystemTray().add(this);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private void setup() {
        this.exitItem = new MenuItem("Exit");
        this.exitItem.addActionListener(this::exitItemActionPerformed);

        this.settingsItem = new MenuItem("Open settings");
        this.settingsItem.addActionListener(this::settingsItemActionPerformed);

        this.localSaveItem = new CheckboxMenuItem("Save image");
        this.localSaveItem.addItemListener(this::localSaveItemStateChanged);

        this.uploadItem = new CheckboxMenuItem("Upload image");
        this.uploadItem.addItemListener(this::uploadItemStateChanged);

        this.popupMenu.add(settingsItem);
        this.popupMenu.addSeparator();
        this.popupMenu.add(localSaveItem);
        this.popupMenu.add(uploadItem);
        this.popupMenu.addSeparator();
        this.popupMenu.add(exitItem);
    }

    public void setLocalSave(boolean localSave) {
        this.localSaveItem.setState(localSave);
    }

    public void setUpload(boolean upload) {
        this.uploadItem.setState(upload);
    }

    private void exitItemActionPerformed(ActionEvent event) {
        System.exit(200);
    }

    private void settingsItemActionPerformed(ActionEvent event) {
        Fireshot.getInstance().getScreenService().getSettingsFrame().setVisible(true);
    }

    private void localSaveItemStateChanged(ItemEvent event) {

    }

    private void uploadItemStateChanged(ItemEvent event) {

    }
}