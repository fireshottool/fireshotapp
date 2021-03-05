package me.fox.ui.components;

import lombok.Getter;
import lombok.Setter;
import me.fox.Fireshotapp;
import me.fox.components.ConfigManager;
import me.fox.components.ResourceManager;
import me.fox.config.Config;
import me.fox.config.ScreenshotConfig;
import me.fox.services.JsonService;
import me.fox.services.ScreenService;

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
public class TrayIcon extends java.awt.TrayIcon implements ResourceManager, ConfigManager {

    private final PopupMenu popupMenu = new PopupMenu();

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

    public void info(String caption, String message) {
        this.displayMessage(caption, message, MessageType.INFO);
    }

    public void warn(String caption, String message) {
        this.displayMessage(caption, message, MessageType.WARNING);
    }

    public void error(String caption, String message) {
        this.displayMessage(caption, message, MessageType.ERROR);
    }

    private void setup() {
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(this::exitItemActionPerformed);

        MenuItem updateItem = new MenuItem("Check for updates...");
        updateItem.addActionListener(this::updateItemActionPerformed);

        MenuItem settingsItem = new MenuItem("Open settings");
        settingsItem.addActionListener(this::settingsItemActionPerformed);

        MenuItem reloadItem = new MenuItem("Reload resources");
        reloadItem.addActionListener(this::reloadItemActionPerformed);

        MenuItem switchItem = new MenuItem("Switch upload and save");
        switchItem.addActionListener(this::switchItemActionPerformed);

        MenuItem versionItem = new MenuItem("Version: " + Fireshotapp.VERSION.get());

        MenuItem screenshotItem = new MenuItem("Take screenshot");
        screenshotItem.addActionListener(this::screenshotItemActionPerformed);

        this.localSaveItem = new CheckboxMenuItem("Save image");
        this.localSaveItem.addItemListener(this::localSaveItemStateChanged);

        this.uploadItem = new CheckboxMenuItem("Upload image");
        this.uploadItem.addItemListener(this::uploadItemStateChanged);

        this.popupMenu.add(screenshotItem);
        this.popupMenu.addSeparator();
        this.popupMenu.add(settingsItem);
        this.popupMenu.add(reloadItem);
        this.popupMenu.addSeparator();
        this.popupMenu.add(this.localSaveItem);
        this.popupMenu.add(this.uploadItem);
        this.popupMenu.add(switchItem);
        this.popupMenu.addSeparator();
        this.popupMenu.add(updateItem);
        this.popupMenu.add(versionItem);
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

    private void screenshotItemActionPerformed(ActionEvent event) {
        ScreenService screenService = Fireshotapp.getInstance().getScreenService();
        if (screenService.getScreenshotFrame().isVisible()) return;
        screenService.show();
    }

    private void switchItemActionPerformed(ActionEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().getJsonService();
        jsonService.getConfig().getScreenshotConfig().setLocalSave(!localSaveItem.getState());
        jsonService.getConfig().getScreenshotConfig().setUpload(!uploadItem.getState());
        jsonService.saveAndApply();
    }

    private void settingsItemActionPerformed(ActionEvent event) {
        Fireshotapp.getInstance().getScreenService().getSettingsFrame().setVisible(true);
    }

    private void localSaveItemStateChanged(ItemEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().getJsonService();
        jsonService.getConfig().getScreenshotConfig().setLocalSave(this.localSaveItem.getState());
        jsonService.saveAndApply();
    }

    private void uploadItemStateChanged(ItemEvent event) {
        JsonService jsonService = Fireshotapp.getInstance().getJsonService();
        jsonService.getConfig().getScreenshotConfig().setUpload(this.uploadItem.getState());
        jsonService.saveAndApply();
    }

    private void updateItemActionPerformed(ActionEvent event) {
        Fireshotapp.getInstance().getUpdateService().checkForUpdate(true);
    }

    private void reloadItemActionPerformed(ActionEvent event) {
        Fireshotapp.getInstance().getFileService().loadResources();
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

    @Override
    public void applyConfig(Config config) {
        ScreenshotConfig screenshotConfig = config.getScreenshotConfig();
        this.setLocalSave(screenshotConfig.isLocalSave());
        this.setUpload(screenshotConfig.isUpload());
    }
}