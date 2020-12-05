package me.fox.ui.components;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshot;
import me.fox.components.ResourceManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author (Ausgefuchster)
 * @version (~ 12.11.2020)
 */

@Getter
@Setter
public class TrayIcon extends java.awt.TrayIcon implements ResourceManager {

    private final PopupMenu popupMenu = new PopupMenu();

    private MenuItem exitItem, settingsItem, reloadItem;
    private CheckboxMenuItem localSaveItem, uploadItem;

    public TrayIcon(String tooltip) {
        super(new BufferedImage(1, 1, 1), tooltip);
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

        this.reloadItem = new MenuItem("Reload resources");
        this.reloadItem.addActionListener(this::reloadItemActionPerformed);

        this.localSaveItem = new CheckboxMenuItem("Save image");
        this.localSaveItem.addItemListener(this::localSaveItemStateChanged);

        this.uploadItem = new CheckboxMenuItem("Upload image");
        this.uploadItem.addItemListener(this::uploadItemStateChanged);

        this.popupMenu.add(settingsItem);
        this.popupMenu.add(reloadItem);
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

    @Override
    public void applyResources(List<File> files) {
        Optional<File> optionalFile = files.stream().
                filter(var -> var.getName().equals("trayIcon.png")).findFirst();
        optionalFile.ifPresent(var -> {
            try {
                Image image = ImageIO.read(var).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                this.setImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

    private void reloadItemActionPerformed(ActionEvent event) {
        Fireshot.getInstance().getFileService().loadResources();
    }
}